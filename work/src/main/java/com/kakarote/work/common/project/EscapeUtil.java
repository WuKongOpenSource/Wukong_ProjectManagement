package com.kakarote.work.common.project;

/**
 * @author zyh
 * 
 * @Description: 转义和反转义工具类
 */
public class EscapeUtil {

	private static final char[][] TEXT = new char[64][];

	static {
		for (int i = 0; i < 64; i++) {
			TEXT[i] = new char[] { (char) i };
		}

		// 单引号
		TEXT['\''] = "&#039;".toCharArray();

		// 双引号
		TEXT['"'] = "&#34;".toCharArray();

		// &符
		TEXT['&'] = "&#38;".toCharArray();

		// 小于号
		TEXT['<'] = "&#60;".toCharArray();

		// 大于号
		TEXT['>'] = "&#62;".toCharArray();
	}

	/**
	 * 清除所有HTML标签，但是不删除标签内的内容
	 * @param content 文本
	 * @return 清除标签后的文本
	 */
	public static String clean(String content) {
		return new HTMLFilter().filter(content);
	}


}
