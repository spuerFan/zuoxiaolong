function searchArticles(name, value) {
	var url = contextPath + '/blog/article_list.ftl?current=1';
	url = url + '&' + name + '=' + encodeURI(value);
	window.location.href=url;
}

/*
 * 滑动到指定元素
 */
function scrollTo(id){
    $('html, body').animate({scrollTop: $(id).offset().top},"fast");
}

/*
 * 取消回复
 */
function cancelReply(){
	$('#reply_div').html('');
	$("#reference_commenter_input").val('');
	$("#reference_comment_id_input").val('');
}

/*
 * 回复评论
 */
function reply(id) {
	var html = '<span>回复['+ $("#commenter_a_" + id).html() +']</span><a id="cancel_reply_button" href="javascript:void(0)">取消回复</a>';
	$("#reply_div").html(html);
	$("#reference_comment_id_input").val(id);
	$("#reference_commenter_input").val($("#commenter_a_" + id).html());
	scrollTo("#comment_textarea");
}

/*
 * 引用评论
 */
function quote(id) {
	reply(id);
	var content = generateQuote(id);
	$("#comment_textarea").val(content);
	scrollTo("#comment_textarea");
}

function generateQuote(id) {
	var content = '[quote]' + $("#comment_content_" + id).html() + '[/quote]';
	return content;
}

/*
 * 评价评论
 */
function comment_remark(commentId,column){
	var articleId = $("#articleId").val();
	var result = null;
	$.ajax({
    	url:contextPath + "/commentRemark.do",
    	async: false,
    	type:"POST",
        data:{"articleId":articleId,"commentId":commentId,"column":column},
        success:function(data){
        	result = data;
        	if(data && data == 'exists') {
        		alert("您已经评价过啦，亲！");
        	}
        }
    });
	if(!result || result != 'success') {
		return;
	}
	if(column == 'good_times') {
		var times = parseInt($("#comment_good_span_"+commentId).html()) + 1;
		$("#comment_good_span_"+commentId).html(times);
	}
	if(column == 'bad_times') {
		var times = parseInt($("#comment_bad_span_"+commentId).html()) + 1;
		$("#comment_bad_span_"+commentId).html(times);
	}
}

/*
 * 评价文章
 */
function remark(){
	var articleId = $("#articleId").val();
	var checked = $("input[name=column]:checked").val();
	var result = null;
	$.ajax({
    	url:contextPath + "/counter.do",
    	async: false,
    	type:"POST",
        data:{"articleId":articleId,"column":checked},
        success:function(data){
        	result = data;
        	if(data && data == 'exists') {
        		alert("您已经评价过啦，亲！");
        	}
        }
    });
	if(!result || result != 'success') {
		return;
	}
	var max = 10;
	$("input[name=column]").each(function(){
		var times = parseInt($("#" + $(this).val() + "_span").html());
		if($(this).val() == checked ) {
			times = times + 1;
			$("#" + $(this).val() + "_span").html(times);
		}
		if (times > max) {
            max = times;
        }
	});
	$("input[name=column]").each(function(){
		var times = parseInt($("#" + $(this).val() + "_span").html());
		$("#" + $(this).val() + "_img").attr("height",times * 50 / max);
	});
}

/*
 * 评论
 */
function comment() {
	var comment = $("#comment_textarea").val();
	var referenceCommentId = $("#reference_comment_id_input").val();
	var referenceCommenter = $("#reference_commenter_input").val();
	if(!comment || !$.trim(comment)) {
		alert("评论不能为空啊，亲！");
		return false;
	}
	$.ajax({
		url:contextPath + "/comment.do",
		type:"POST",
		data:{"articleId":$("#articleId").val(),"content":comment,"referenceCommentId":referenceCommentId,"referenceCommenter":referenceCommenter},
		success:function(data) {
			if (data && data.success) {
				var size = parseInt($("#comment_size").html()) + 1;
				var id = data.id;
				$("#comment_size").html(size);
				if(size == 1) {
					$("#comment_list").html('');
				}
				appendComment(id,data.content,size);
				$("#comment_textarea").val('');
				cancelReply();
			} else {
				alert(data);
			}
		}
	});
}

/*
 * 追加评论
 */
function appendComment(id,content,size){
	var html = '<div id="comment_div_' + id + '" class="feedbackItem">';
	html = html + '<div class="feedbackListSubtitle">';
	html = html + '<a href="javascript:void(0)" class="layer">#' + size + '楼</a>  <span class="comment_date">刚刚</span> ';
	html = html + '</div>';
	html = html + '<div class="feedbackCon">';
	html = html + '<div class="blog_comment_body">' + content + '</div>';
	html = html + '</div>';
	html = html + '</div>';
	$("#comment_list").append(html);
}
