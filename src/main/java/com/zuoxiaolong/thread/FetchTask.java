package com.zuoxiaolong.thread;

import com.zuoxiaolong.config.Configuration;
import com.zuoxiaolong.generator.Generators;
import com.zuoxiaolong.reptile.Cnblogs;
import com.zuoxiaolong.search.LuceneHelper;
import com.zuoxiaolong.util.ImageUtil;
import org.apache.log4j.Logger;

/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author 左潇龙
 * @since 2015年5月25日 下午9:11:26
 */
public class FetchTask implements Runnable {

	private static final Logger logger = Logger.getLogger(FetchTask.class);
	
	private static final int THREAD_SLEEP_MINUTES = Integer.valueOf(Configuration.get("fetch.thread.sleep.minutes"));

	@Override
	public void run() {
		while (true) {
			try {
				ImageUtil.loadArticleImages();
				if (Configuration.isProductEnv()) {
					Cnblogs.fetchArticlesAfterLogin();
				} else {
					Cnblogs.fetchArticlesCommon();
				}
				LuceneHelper.updateIndex();
				Generators.generate();
				Thread.sleep(1000 * 60 * THREAD_SLEEP_MINUTES);
			} catch (Exception e) {
				logger.warn("fetch and generate failed ...", e);
				break;
			}
		}
	}
	
}
