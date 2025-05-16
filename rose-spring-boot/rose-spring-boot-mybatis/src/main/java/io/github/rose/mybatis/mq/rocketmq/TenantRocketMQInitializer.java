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
package io.github.rose.mybatis.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 多租户的 RocketMQ 初始化器
 *
 * @author EnjoyIot
 */
public class TenantRocketMQInitializer implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DefaultRocketMQListenerContainer) {
            DefaultRocketMQListenerContainer container = (DefaultRocketMQListenerContainer) bean;
            initTenantConsumer(container.getConsumer());
        } else if (bean instanceof RocketMQTemplate) {
            RocketMQTemplate template = (RocketMQTemplate) bean;
            initTenantProducer(template.getProducer());
        }
        return bean;
    }

    private void initTenantProducer(DefaultMQProducer producer) {
        if (producer == null) {
            return;
        }
        DefaultMQProducerImpl producerImpl = producer.getDefaultMQProducerImpl();
        if (producerImpl == null) {
            return;
        }
        producerImpl.registerSendMessageHook(new TenantRocketMQSendMessageHook());
    }

    private void initTenantConsumer(DefaultMQPushConsumer consumer) {
        if (consumer == null) {
            return;
        }
        DefaultMQPushConsumerImpl consumerImpl = consumer.getDefaultMQPushConsumerImpl();
        if (consumerImpl == null) {
            return;
        }
        consumerImpl.registerConsumeMessageHook(new TenantRocketMQConsumeMessageHook());
    }
}
