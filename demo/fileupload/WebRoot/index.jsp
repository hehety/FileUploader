<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="javax.naming.*"%>
<%@ page language="java" import="com.demo.ejb.beans.*"%>
<%
	try {
			InitialContext tcx = new InitialContext();
			UserDao userDao = (UserDao) tcx.lookup("UserDaoImpl/local");
			User user = new User();
			user.setAddress("上海市===123");
			user.setDescription("nice!!!");
			user.setEmail("hehety@outlook.com");
			user.setPassword("123456");
			user.setPhone("13611991143");
			user.setUsername("hehety1232132");
			//user.setId(189350);
			userDao.save(user);
			//userDao.delete(189348);
			//userDao.update(user);
			//userDao.delete(user);
			response.getWriter().write("insert okk");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
%>
