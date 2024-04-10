

package com.chain4travel.cmbplugin.cache;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.protobuf.GeneratedMessageV3;

import build.buf.gen.cmp.services.book.v1alpha.ValidationRequest;
import build.buf.gen.cmp.services.book.v1alpha.ValidationResponse;
import jakarta.annotation.PostConstruct;


@Service
public class CacheService {

    private final static Logger     logger = LoggerFactory.getLogger(CacheService.class);

    @Value("${cmbplugin.cache.search.ttl}")
    private long                    searchTTL;

    @Value("${cmbplugin.cache.validation.ttl}")
    private long                    validationTTL;

    @Value("${cmbplugin.cache.storageDir}")
    private String                  storageDir;

    private Path                    searchCacheDataPath;
    private Path                    validationCacheDataPath;
    private Path                    cacheEntryPath;

    private Cache<UUID, CacheEntry> searchCache;
    private Cache<UUID, CacheEntry> validationCache;


    @PostConstruct
    private void setup() {
        searchCache = Caffeine.newBuilder().expireAfterWrite(searchTTL, TimeUnit.SECONDS).removalListener((UUID key, CacheEntry value, RemovalCause cause) -> onRemoveCacheEntry(value)).build();
        validationCache = Caffeine.newBuilder().expireAfterWrite(validationTTL, TimeUnit.SECONDS).removalListener((UUID key, CacheEntry value, RemovalCause cause) -> onRemoveCacheEntry(value)).build();

        searchCacheDataPath = Paths.get(storageDir, "search");
        validationCacheDataPath = Paths.get(storageDir, "validation");
        cacheEntryPath = Paths.get(storageDir, "entries");

        try {
            if (Files.notExists(searchCacheDataPath)) {
                Files.createDirectories(searchCacheDataPath);
            }
            if (Files.notExists(validationCacheDataPath)) {
                Files.createDirectories(validationCacheDataPath);
            }
            if (Files.notExists(cacheEntryPath)) {
                Files.createDirectories(cacheEntryPath);
            }
        }
        catch (IOException e) {
            logger.error("Could not create cache directories.", e);
        }

        try {
            var cacheEntryFiles = Files.newDirectoryStream(cacheEntryPath, "*.bin");
            cacheEntryFiles.forEach(cacheEntryFilePath -> {
                try {
                    var fileInputStream = Files.newInputStream(cacheEntryFilePath, StandardOpenOption.READ);
                    var objectInputStream = new ObjectInputStream(fileInputStream);
                    var cacheEntryFromFile = (CacheEntry) objectInputStream.readObject();
                    var cacheEntry = new CacheEntry(cacheEntryFromFile);

                    switch (cacheEntry.cacheDataType) {
                        case search:
                            searchCache.put(cacheEntry.id, cacheEntry);
                            break;
                        case validation:
                            validationCache.put(cacheEntry.id, cacheEntry);
                            break;
                    }
                }
                catch (Exception e) {
                    logger.warn("Could not read cache entry from file {}.", cacheEntryFilePath, e);
                }
            });
        }
        catch (IOException e) {
            logger.warn("Could not rebuild cache from cache entries in file system!", e);
        }
    }


    public CacheSearchType getCacheSearchType(UUID searchId) {
        var cacheEntry = getCacheEntryIfPresent(searchCache, searchTTL, searchId);
        return cacheEntry != null ? cacheEntry.cacheSearchType : null;
    }


    public InputStream readSearchRequestData(UUID searchId) {
        var cacheEntry = getCacheEntryIfPresent(searchCache, searchTTL, searchId);
        return readDataIfPresent(cacheEntry, CacheDataContentType.request);
    }


    public InputStream readSearchResponseData(UUID searchId) {
        var cacheEntry = getCacheEntryIfPresent(searchCache, searchTTL, searchId);
        return readDataIfPresent(cacheEntry, CacheDataContentType.response);
    }


    public void cacheSearchData(UUID searchId, CacheSearchType type, GeneratedMessageV3 requestMessage, GeneratedMessageV3 responseMessage) {
        cacheData(searchId, CacheDataType.search, type, requestMessage, responseMessage);
    }


    public InputStream readValidationRequestData(UUID validationId) {
        var cacheEntry = getCacheEntryIfPresent(validationCache, validationTTL, validationId);
        return readDataIfPresent(cacheEntry, CacheDataContentType.request);
    }


    public InputStream readValidationResponseData(UUID validationId) {
        var cacheEntry = getCacheEntryIfPresent(validationCache, validationTTL, validationId);
        return readDataIfPresent(cacheEntry, CacheDataContentType.response);
    }


    public void cacheValidationData(UUID validationId, ValidationRequest request, ValidationResponse response) {
        cacheData(validationId, CacheDataType.validation, null, request, response);
    }


    private InputStream readDataIfPresent(CacheEntry cacheEntry, CacheDataContentType cacheDataContentType) {
        InputStream inputStream = null;

        if (cacheEntry != null) {
            var path = getDataPath(cacheEntry.id, cacheEntry.cacheDataType, cacheDataContentType);

            if (Files.exists(path)) {
                try {
                    inputStream = Files.newInputStream(path, StandardOpenOption.READ);
                }
                catch (IOException e) {
                    logger.error("Could not create input stream!", e);
                }
            }
            else {
                logger.warn("Cache file does not exist.");
            }
        }
        else {
            logger.debug("Cache entry does not exist.");
        }

        return inputStream;
    }


    private void cacheData(UUID id, CacheDataType cacheDataType, CacheSearchType cacheSearchType, GeneratedMessageV3 requestMessage, GeneratedMessageV3 responseMessage) {
        var requestPath = getDataPath(id, cacheDataType, CacheDataContentType.request);
        var responsePath = getDataPath(id, cacheDataType, CacheDataContentType.response);
        var fileWriteSuccessful = false;

        try {
            var requestOutputStream = Files.newOutputStream(requestPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            requestMessage.writeTo(requestOutputStream);
            requestOutputStream.flush();
            requestOutputStream.close();

            var responseOutputStream = Files.newOutputStream(responsePath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            responseMessage.writeTo(responseOutputStream);
            responseOutputStream.flush();
            responseOutputStream.close();

            fileWriteSuccessful = true;
        }
        catch (IOException e) {
            logger.error("Could not write cache data to file system!", e);
        }

        if (fileWriteSuccessful) {
            var cacheEntry = new CacheEntry(id, cacheDataType, cacheSearchType);

            switch (cacheDataType) {
                case search:
                    searchCache.put(id, cacheEntry);
                    break;
                case validation:
                    validationCache.put(id, cacheEntry);
                    break;
            }

            try {
                var cacheEntryPath = getCacheEntryPath(cacheEntry.id);
                var cacheEntryOutputStream = Files.newOutputStream(cacheEntryPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                var cacheEntryObjectStream = new ObjectOutputStream(cacheEntryOutputStream);
                cacheEntryObjectStream.writeObject(cacheEntry);
                cacheEntryOutputStream.flush();
                cacheEntryOutputStream.close();
            }
            catch (IOException e) {
                logger.warn("Could not write cache entry to file system!", e);
            }
        }
    }


    private CacheEntry getCacheEntryIfPresent(Cache<UUID, CacheEntry> cache, long ttlSeconds, UUID id) {
        var cacheEntry = cache.getIfPresent(id);
        var cacheEntryExpired = false;

        if (cacheEntry != null && cacheEntry.checkExpirationManually) {
            var lifeTime = System.currentTimeMillis() - cacheEntry.creationTimestamp;
            cacheEntryExpired = lifeTime > ttlSeconds * 1000;
        }

        if (cacheEntryExpired) {
            cache.invalidate(id);
            return null;
        }
        else {
            return cacheEntry;
        }
    }


    private void onRemoveCacheEntry(CacheEntry cacheEntry) {
        logger.debug("Remove cache entry with id {}", cacheEntry.id);

        var requestPath = getDataPath(cacheEntry.id, cacheEntry.cacheDataType, CacheDataContentType.request);
        var responsePath = getDataPath(cacheEntry.id, cacheEntry.cacheDataType, CacheDataContentType.response);
        var cacheEntryPath = getCacheEntryPath(cacheEntry.id);

        try {
            Files.deleteIfExists(requestPath);
            Files.deleteIfExists(responsePath);
            Files.deleteIfExists(cacheEntryPath);
        }
        catch (IOException e) {
            logger.error("Could not delete cache data from file system after cache removal!", e);
        }
    }


    private Path getCacheEntryPath(UUID id) {
        var filename = String.format("%s.bin", id);
        return Paths.get(cacheEntryPath.toString(), filename);
    }


    private Path getDataPath(UUID id, CacheDataType cacheDataType, CacheDataContentType cacheDataContentType) {
        var basePath = switch (cacheDataType) {
            case search: {
                yield searchCacheDataPath;
            }
            case validation: {
                yield validationCacheDataPath;
            }
        };

        var suffix = switch (cacheDataContentType) {
            case request: {
                yield "req";
            }
            case response: {
                yield "res";
            }
        };

        var filename = String.format("%s-%s.bin", id, suffix);
        return Paths.get(basePath.toString(), filename);
    }


    private static enum CacheDataType {
        search,
        validation;
    }


    private static enum CacheDataContentType {
        request,
        response;
    }


    private static class CacheEntry implements Serializable {

        private static final long serialVersionUID        = 1L;

        public UUID               id;
        public CacheDataType      cacheDataType;
        public CacheSearchType    cacheSearchType;
        public long               creationTimestamp       = System.currentTimeMillis();
        public boolean            checkExpirationManually = false;


        public CacheEntry(UUID id, CacheDataType cacheDataType, CacheSearchType cacheSearchType) {
            this.id = id;
            this.cacheDataType = cacheDataType;
            this.cacheSearchType = cacheSearchType;
        }


        public CacheEntry(CacheEntry cacheEntry) {
            this.id = cacheEntry.id;
            this.cacheDataType = cacheEntry.cacheDataType;
            this.cacheSearchType = cacheEntry.cacheSearchType;
            this.creationTimestamp = cacheEntry.creationTimestamp;
            this.checkExpirationManually = true;
        }
    }
}
