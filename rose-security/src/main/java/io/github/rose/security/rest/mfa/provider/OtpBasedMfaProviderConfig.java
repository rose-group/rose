/*
 * Copyright © 2025 rose-group.github.io
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
package io.github.rose.security.rest.mfa.provider;

import javax.validation.constraints.Min;

public abstract class OtpBasedMfaProviderConfig implements MfaProviderConfig {

    @Min(value = 1, message = "is required")
    private int verificationCodeExpireTime;

    public int getVerificationCodeExpireTime() {
        return verificationCodeExpireTime;
    }

    public void setVerificationCodeExpireTime(int verificationCodeExpireTime) {
        this.verificationCodeExpireTime = verificationCodeExpireTime;
    }

    @Override
    public String toString() {
        return "OtpBasedMfaProviderConfig{" +
            "verificationCodeExpireTime=" + verificationCodeExpireTime +
            '}';
    }
}
