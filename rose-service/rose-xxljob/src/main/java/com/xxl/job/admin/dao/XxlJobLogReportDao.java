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
package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * job log
 *
 * @author xuxueli 2019-11-22
 */
@Mapper
public interface XxlJobLogReportDao {

    public int save(XxlJobLogReport xxlJobLogReport);

    public int update(XxlJobLogReport xxlJobLogReport);

    public List<XxlJobLogReport> queryLogReport(
        @Param("triggerDayFrom") Date triggerDayFrom, @Param("triggerDayTo") Date triggerDayTo);

    public XxlJobLogReport queryLogReportTotal();
}
