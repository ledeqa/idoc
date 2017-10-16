package com.idoc.shiro.token;

import java.sql.Timestamp;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.idoc.model.UserModel;
import com.idoc.service.login.LoginIndexServiceImpl;

/**
 * shiro 认证 + 授权 重写
 * 
 * @author bjhuwei1
 * @version 创建时间：2017年7月19日 下午4:26:36
 */
public class SampleRealm extends AuthorizingRealm {
	
	@Autowired
	private LoginIndexServiceImpl loginIndexServiceImpl;
	
	/** 
     * 授权 
     */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//		Long userId = TokenManager.getUserId();
//		// 根据用户ID查询角色（role），放入到Authorization里。
//		List<URole> roleList = roleService.findRoleByUserId(userId);
//		Set<String> roles = null;
//		if(roleList != null && !roleList.isEmpty()){
//			roles = new HashSet<>();
//			for(URole role : roleList){
//				roles.add(role.getType());
//			}
//		}
//		info.setRoles(roles);
//		// 根据用户ID查询权限（permission），放入到Authorization里。
//		Set<String> permissions = permissionService.findPermissionByUserId(userId);
//		info.setStringPermissions(permissions);
		return info;
	}

	/**
	 *  认证信息，主要针对用户登录， 
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		ShiroToken token = (ShiroToken) authcToken;
		UserModel user = loginIndexServiceImpl.login(token.getUsername(), token.getPswd());
		if (null == user) {
			throw new AccountException("帐号或密码不正确！");
		} else if (user.getStatus() == UserModel._0) {
			throw new DisabledAccountException("帐号已经禁止登录！");
		} else {
			// 更新登录时间 last login time
			user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			loginIndexServiceImpl.updateUserSelective(user);
		}
		return new SimpleAuthenticationInfo(user, user.getPswd(), getName());
	}

	/**
	 * 清空当前用户权限信息
	 */
	public void clearCachedAuthorizationInfo() {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principalCollection, getName());
		super.clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 指定principalCollection 清除
	 */
	public void clearCachedAuthorizationInfo(PrincipalCollection principalCollection) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principalCollection, getName());
		super.clearCachedAuthorizationInfo(principals);
	}

}
