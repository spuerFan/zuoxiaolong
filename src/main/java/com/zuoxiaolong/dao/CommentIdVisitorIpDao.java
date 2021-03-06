package com.zuoxiaolong.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * @since 2015年5月12日 下午5:47:40
 */
public abstract class CommentIdVisitorIpDao extends BaseDao {

	public static boolean save(final int commentId, final String visitorIp, final String username) {
		return execute(new TransactionalOperation<Boolean>() {
			@Override
			public Boolean doInConnection(Connection connection) {
				try {
					PreparedStatement statement = connection.prepareStatement("insert into comment_id_visitor_ip (visitor_ip,comment_id,username) values (?,?,?)");
					statement.setString(1, visitorIp);
					statement.setInt(2, commentId);
					statement.setString(3, username);
					int result = statement.executeUpdate();
					return result > 0;
				} catch (SQLException e) {
					error("save remarkVisitorIp failed ..." , e);
				}
				return false;
			}
		});
	} 
	
	public static boolean exsits(final int commentId, final String visitorIp, final String username) {
		return execute(new Operation<Boolean>() {
			@Override
			public Boolean doInConnection(Connection connection) {
				try {
					PreparedStatement statement = connection.prepareStatement("select * from comment_id_visitor_ip where visitor_ip=? and comment_id=?");
					statement.setString(1, visitorIp);
					statement.setInt(2, commentId);
					ResultSet resultSet = statement.executeQuery();
					boolean result = resultSet.next();
					statement = connection.prepareStatement("select * from comment_id_visitor_ip where username=? and comment_id=?");
					statement.setString(1, username);
					statement.setInt(2, commentId);
					resultSet = statement.executeQuery();
					result = result || resultSet.next();
					return result;
				} catch (SQLException e) {
					error("query remarkVisitorIp failed ..." , e);
				}
				return false;
			}
		});
	}
	
}
