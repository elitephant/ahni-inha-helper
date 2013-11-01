package kr.ac.inha.itlab.daegikim.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class InhaPortalAuthenticator implements Runnable {

    //HTTP Request 를 보낼때 필요한 Form Date Fields
    //dest = 인증 후 redirect 되는 URL. 여기서는 의미 없음.
    //uid = base64 로 인코딩 된 유저 아이디(학번). 아래에서 {uid} 부분을 인코딩 된 문자열로 replace 함
    //pwd = base64 로 인코딩 된 유저 패스워드. 아래에서 {pwd} 부분을 인코딩 된 문자열로 replace 함
    private static final String REQUEST_FORM_DATA = "dest=http%3A%2F%2Fwww.inha.ac.kr&uid={uid}&pwd={pwd}";

    //HTTP POST Request 을 보내는 target URL
    private static final String LOGIN_URL = "https://www.inha.ac.kr/common/asp/login/loginProcess.asp";

    //인증 성공시 HTML 에 포함되어 있는 문자열
    private static final String LOGIN_SUCCESS_MESSAGE = "로그인 되었습니다";

    //로깅을 위한 log4j 객체
    private static final Logger logger = Logger.getLogger(InhaPortalAuthenticator.class.getName());

    @Override
    public void run() {
        //uid pwd Base64 인코딩
        String uid = new String(Base64.encodeBase64("12101387".getBytes()));
        String pwd = new String(Base64.encodeBase64("xr7788kr".getBytes()));

        //HTTP 요청
        if(getHtml(uid, pwd)) {
            logger.info("로그인에 성공하였습니다.");
        } else {
            logger.error("로그인에 실패하였습니다.");
        }
    }

    private boolean getHtml(String uid, String pwd) {
        boolean result = false;

        HttpURLConnection hConnection = null;
        PrintStream ps = null;
        InputStream is = null;
        BufferedReader in = null;

        try{
            //TODO: getOutputStream, getInputStream 을 한번으로 줄일 수 있음?
            URL url = new URL(LOGIN_URL);
            hConnection = (HttpURLConnection)url.openConnection();
            hConnection.setInstanceFollowRedirects(false);
            hConnection.setDoOutput(true);
            hConnection.setRequestMethod("POST");
            ps = new PrintStream(hConnection.getOutputStream());


            //인코딩된 uid, pwd 로 문자열 replace
            ps.print(REQUEST_FORM_DATA.replace("{uid}", uid).replace("{pwd}", pwd));

            if((is = hConnection.getInputStream()) != null)
            {
                in = new BufferedReader(new InputStreamReader(is, Charset.forName("EUC-KR")));
                String readLine;

                while((readLine=in.readLine()) != null)
                {
                    if(readLine.contains(LOGIN_SUCCESS_MESSAGE)) {
                        result = true;
                        break;
                    }
                }
            }
            else {
                logger.error("Connection failed...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //스트림 닫기
            hConnection.disconnect();
            closeQuietly(is);
            closeQuietly(ps);
            closeQuietly(in);
        }

        return result;
    }
}
