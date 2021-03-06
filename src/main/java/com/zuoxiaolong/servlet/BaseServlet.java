package com.zuoxiaolong.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.zuoxiaolong.freemarker.ArticleHelper;
import com.zuoxiaolong.freemarker.ArticleListHelper;
import com.zuoxiaolong.freemarker.IndexHelper;

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
 * @since 2015年5月10日 上午1:30:40
 */
public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = -6921810339176306346L;
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	private static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();
	
	private static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestThreadLocal.set(request);
		response.setCharacterEncoding("UTF-8");
		responseThreadLocal.set(response);
		service();
	}
	
	protected String getUsername() {
		Map<String, String> user = getUser();
		if (user != null) {
			return user.get("username");
		}
		return null;
	}

	protected Map<String, String> getUser() {
		return getUser(getRequest());
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("user") != null) {
			return (Map<String, String>) session.getAttribute("user");
		}
		return null;
	}

	private static final Pattern STATIC_ARTICLE_PATTERN = Pattern.compile("html/article_[0-9]+\\.html");

	private static final Pattern STATIC_ARTICLE_LIST_PATTERN = Pattern.compile("/html/article_list_[a-zA-Z_]+_[0-9]+\\.html");

	protected String getDynamicUrl() {
		String requestUri = getRequest().getHeader("Referer");
		Matcher matcher = STATIC_ARTICLE_PATTERN.matcher(requestUri);
		if (matcher.find()) {
			return ArticleHelper.generateDynamicPath(matcher.group());
		}
		matcher = STATIC_ARTICLE_LIST_PATTERN.matcher(requestUri);
		if (matcher.find()) {
			return ArticleListHelper.generateDynamicPath(matcher.group());
		}
		return IndexHelper.generateDynamicPath();
	}

	protected abstract void service() throws ServletException, IOException ;
	
	protected HttpServletRequest getRequest() {
		return requestThreadLocal.get();
	}
	
	protected HttpServletResponse getResponse() {
		return responseThreadLocal.get();
	}

	protected void writeJsonObject(Object object) {
		getResponse().setContentType("application/json");
		writeText(JSONObject.fromObject(object).toString());
	}

	protected void writeJsonArray(List<?> list) {
		getResponse().setContentType("application/json");
		writeText(JSONArray.fromObject(list).toString());
	}

	protected void writeText(String text){
		PrintWriter printWriter = null;
		try {
			printWriter = getResponse().getWriter();
			printWriter.write(text);
			printWriter.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
