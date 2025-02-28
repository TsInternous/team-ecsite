package jp.co.internous.team2412.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.internous.team2412.model.domain.MstUser;
import jp.co.internous.team2412.model.mapper.MstUserMapper;
import jp.co.internous.team2412.model.session.LoginSession;

/**
 * マイページに関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2412/mypage")
public class MyPageController {
	
	/*
	 * フィールド定義
	 */
	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private LoginSession loginSession;
	
	/**
	 * マイページ画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return マイページ画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		
	    String loggedInUserName = loginSession.getUserName();
	    String loggedInPassword = loginSession.getPassword(); 
	    
	    if (loggedInUserName == null || loggedInUserName.isEmpty()) {
	        return "redirect:/login"; 
	    }
	    
	    MstUser user = userMapper.findByUserNameAndPassword(loggedInUserName, loggedInPassword);
	    m.addAttribute("loginSession", loginSession);
	    
	    if (user != null) {
	        m.addAttribute("user", user);
	    }
	    
	    return "my_page";
	}
}
