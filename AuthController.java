package jp.co.internous.team2412.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import jp.co.internous.team2412.model.domain.MstUser;
import jp.co.internous.team2412.model.form.UserForm;
import jp.co.internous.team2412.model.mapper.MstUserMapper;
import jp.co.internous.team2412.model.mapper.TblCartMapper;
import jp.co.internous.team2412.model.session.LoginSession;



/**
 * 認証に関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@RestController
@RequestMapping("/team2412/auth")
public class AuthController {
	
	/*
	 * フィールド定義
	 */
	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private LoginSession loginSession;
	
	@Autowired
    private TblCartMapper tblCartMapper;
	
	private Gson gson = new Gson();
	
		
	/**
	 * ログイン処理をおこなう
	 * @param f ユーザーフォーム
	 * @return ログインしたユーザー情報(JSON形式)
	 */
	@PostMapping("/login")
	public String login(@RequestBody UserForm f) {
		
        MstUser user = userMapper.findByUserNameAndPassword(f.getUserName(), f.getPassword());
        
        if (user == null) {
            return gson.toJson("");
        }
        
        loginSession.setUserId(user.getId());
        loginSession.setLogined(true);
        loginSession.setUserName(user.getUserName());
        loginSession.setPassword(user.getPassword());
     
        if (loginSession.getTmpUserId() != 0) {
            tblCartMapper.updateUserId(loginSession.getUserId (),loginSession.getTmpUserId());
            loginSession.setTmpUserId(0);
        }
        
        return gson.toJson(user);
	}
	
	/**
	 * ログアウト処理をおこなう
	 * @return 空文字
	 */
	@PostMapping("/logout")
	public String logout() {
		
        loginSession.setUserId(0);
        loginSession.setUserName(null);
        loginSession.setLogined(false);
        
        return ""; 
    }

	/**
	 * パスワード再設定をおこなう
	 * @param f ユーザーフォーム
	 * @return 処理後のメッセージ
	 */
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestBody UserForm f) {
		
	    String loggedInUserName = loginSession.getUserName();
	    String currentPassword = loginSession.getPassword();
	    
	    MstUser user = userMapper.findByUserNameAndPassword(loggedInUserName, currentPassword);
	    
	    if (user == null) {
	        return "ユーザー情報が見つかりません。";
	    }
	    
	    if (currentPassword.equals(f.getNewPassword())) {
	        return "現在のパスワードと同一文字列が入力されました。";
	    }
	    
	    int updateResult = userMapper.updatePassword(loggedInUserName, f.getNewPassword());
	    
	    if (updateResult > 0) {
	        loginSession.setPassword(f.getNewPassword());
	        return "パスワードが再設定されました。";
	    } else {
	        return "パスワードの再設定に失敗しました。";
	    }
	}
}