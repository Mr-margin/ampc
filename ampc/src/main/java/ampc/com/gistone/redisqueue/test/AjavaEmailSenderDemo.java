package ampc.com.gistone.redisqueue.test;

/*
功能:加入了参考文章中没有的发送附件的功能.本机测试通过.
参考文章:http://ajava.org/article-722-1.html
使用Javamail发送邮件例子及解释 

 2012-2-1 00:14| 发布者: mark| 查看: 236| 评论: 0|原作者: mark|来自: ajava.org
摘要: 下面例子演示怎样用javamail来发送邮件，在测试之前，我们要下载javamail的类包，并添加入你的工程中，如果你的IDE自带javamail的类包，那就很简单，直接import 就行，我使用的是MyEclipse 7.5，自带，所以可以直接 ...
下面例子演示怎样用javamail来发送邮件，在测试之前，我们要下载javamail的类包，并添加入你的工程中，如果你的IDE自带javamail的类包，那就很简单，直接import 就行，mark使用的是MyEclipse 7.5，自带，所以可以直接测试下面代码了。

 

几个javamail类的作用
javax.mail.Properties类 
我们使用Properties来创建一个session对象。里面保存里对Session的一些设置，如协议，SMTP地址，是否验证的设置信息 

javax.mail.Session类 
代表一个邮件session. 有session才有通信。

javax.mail.InternetAddress类 
Address确定信件地址。

javax.mail.MimeMessage类 
Message对象将存储发送的电子邮件信息，如主题，内容等等

javax.mail.Transport类 
 Transport传输邮件类，采用send方法是发送邮件。
 * */

import java.util.Properties;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

//JavaMail发送例子
public class AjavaEmailSenderDemo {
	public static void main(String[] args) {

		String stmp = "smtp.sina.com";

		String from = "yanglei_xu@163.com";// 发信邮箱
		String to = "2372030362@qq.com";// 收信邮箱

		String username = "yanglei_xu@163.com";
		String password = "ylec171926.";

		String subject = "邮件主题:你好,这是一封测试邮件(含附件).";// 邮件主题
		String text = "你好！";// 邮件内容

		AjavaSendMail sm = new AjavaSendMail(stmp);

		sm.setNamePass(username, password);
		sm.setSubject(subject);
		sm.setFrom(from);
		sm.setTo(to);
		sm.setText(text);
		sm.addFileAffix("d:/mail_attach.txt");
		sm.createMimeMessage();
		sm.setOut();
	}
}
