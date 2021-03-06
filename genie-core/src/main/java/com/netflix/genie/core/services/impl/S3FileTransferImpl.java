/*
 *
 *  Copyright 2015 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.core.services.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.netflix.genie.common.exceptions.GenieException;
import com.netflix.genie.common.exceptions.GenieServerException;
import com.netflix.genie.core.services.FileTransfer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of the FileTransferService interface in which the remote locations are on Amazon S3.
 *
 * @author amsharma
 * @since 3.0.0
 */
@Slf4j
public class S3FileTransferImpl implements FileTransfer {

    private AmazonS3Client s3Client;

    private final Pattern s3FilePattern =
        Pattern.compile("^(s3[n]?://)(.*?)/(.*/.*)");

    private final Pattern s3PrefixPattern =
        Pattern.compile("^s3[n]?://.*$");

    /**
     * Constructor.
     *
     * @param amazonS3Client An amazon s3 client object
     * @throws GenieException If there is a problem
     */
    @Autowired
    public S3FileTransferImpl(
        final AmazonS3Client amazonS3Client
        ) throws GenieException {
        this.s3Client = amazonS3Client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String fileName) throws GenieException {
        log.debug("Called with file name {}", fileName);
        final Matcher matcher =
            s3PrefixPattern.matcher(fileName);
        return matcher.matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFile(
        @NotBlank (message = "Source file path cannot be empty.")
        final String srcRemotePath,
        @NotBlank (message = "Destination local path cannot be empty")
        final String dstLocalPath
    ) throws GenieException {
        log.debug("Called with src path {} and destination path {}", srcRemotePath, dstLocalPath);

        final Matcher matcher = s3FilePattern.matcher(srcRemotePath);
        if (matcher.matches()) {
            final String bucket = matcher.group(2);
            final String key = matcher.group(3);

            try {
                s3Client.getObject(
                    new GetObjectRequest(bucket, key),
                    new File(dstLocalPath));
            } catch (AmazonS3Exception ase) {
                log.error("Error fetching file {} from s3 due to exception {}", srcRemotePath, ase);
                throw new GenieServerException("Error downloading file from s3. Filename: " + srcRemotePath);
            }
        } else {
            throw new GenieServerException("Invalid path for s3 file" + srcRemotePath);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putFile(
        @NotBlank (message = "Source local path cannot be empty.")
        final String srcLocalPath,
        @NotBlank (message = "Destination remote path cannot be empty")
        final String dstRemotePath
    ) throws GenieException {
        log.debug("Called with src path {} and destination path {}", srcLocalPath, dstRemotePath);

        final Matcher matcher = s3FilePattern.matcher(dstRemotePath);
        if (matcher.matches()) {
            final String bucket = matcher.group(2);
            final String key = matcher.group(3);

            try {
                s3Client.putObject(bucket, key, new File(srcLocalPath));
            } catch (AmazonS3Exception ase) {
                log.error("Error posting file {} to s3 due to exception {}", dstRemotePath, ase);
                throw new GenieServerException("Error uploading file to s3. Filename: " + dstRemotePath);
            }
        } else {
            throw new GenieServerException("Invalid path for s3 file" + dstRemotePath);
        }
    }
}
