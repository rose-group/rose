/*
 * Copyright © 2025 rosestack.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rose.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class CredentialsExpiredResponse extends AuthenticationServiceException {

    private final String resetToken;

    protected CredentialsExpiredResponse(String message, String resetToken) {
        super(message);
        this.resetToken = resetToken;
    }

    public static CredentialsExpiredResponse of(final String message, final String resetToken) {
        return new CredentialsExpiredResponse(message, resetToken);
    }

    public String getResetToken() {
        return resetToken;
    }
}
