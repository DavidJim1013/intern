package com.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.util.DividePage;
import com.util.UUIDTools;

@SuppressWarnings("serial")
public class ClientAction extends HttpServlet {

	private ClientService service;

	/**
	 * Constructor of the object.
	 */
	public ClientAction() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request  the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException      if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request  the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException      if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();

		String action_flag = request.getParameter("action_flag");
		if (action_flag.equals("add")) {
			addClient(request, response);
		} else if (action_flag.equals("search")) {
			listClient(request, response);
		} else if (action_flag.equals("del")) {
			delClient(request, response);
		} else if (action_flag.equals("viewUpdate")) {
			viewUpdateClient(request, response);
		} else if (action_flag.equals("update")) {
			updateClient(request, response);
		}

		out.flush();
		out.close();
	}

	private void viewUpdateClient(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String clientid = request.getParameter("clientid");
		Map<String, Object> map = service.viewClient(clientid);
		request.setAttribute("clientMap", map);
		try {
			request.getRequestDispatcher("/updateInf.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @param response
	 */
	private void delClient(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		// ??????????????????
		String[] ids = request.getParameterValues("ids");
		for (int i = 0; i < ids.length; i++) {
			System.out.println("ids[" + i + "]=" + ids[i]);
		}
		boolean flag = service.delClient(ids);
		System.out.println("??????flag:" + flag);
		if (flag) {
			try {
				request.getRequestDispatcher("/main.jsp").forward(request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void listClient(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

		String clientName = request.getParameter("clientName");
		String clientSex = request.getParameter("Searchsex");
		String clientDOB = request.getParameter("SearchDOB");
		System.out.print("clientName:"+clientName+" clientSex:"+clientSex+" clientDOB:"+clientDOB);
		String pageNum = request.getParameter("pageNum");
		System.out.println("?????? pageNum :" + pageNum);
		if (clientName == null) {
			clientName = "";
		}
		if (clientSex == null) {
			clientSex = "";
		}
		if (clientDOB == null) {
			clientDOB = "";
		}
		
		
		int totalRecord = service.getItemCount(clientName,clientSex,clientDOB); // ???????????????
		int currentPage = 1;
		
		DividePage dividePage = new DividePage(5, totalRecord);// ?????????????????????
		if (pageNum != null) {
			currentPage = Integer.parseInt(pageNum);
			dividePage.setCurrentPage(currentPage);
		}

		// ???????????????
		int start = dividePage.fromIndex();
		// ????????????
		int end = dividePage.toIndex();

		System.out.println("currentPageNum :" + dividePage.getCurrentPage() + ", start = " + start + ", end = " + end);

		List<Map<String, Object>> list = null;
		try {
			list = service.listClient(clientName,clientSex,clientDOB, start, end);
			request.setAttribute("listClient", list);
			request.setAttribute("dividePage", dividePage);
			request.setAttribute("clientName", clientName);
			request.getRequestDispatcher("/main.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// ????????????
	private void addClient(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ??????????????????????????????????????????????????????
		String path = request.getContextPath();
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		servletFileUpload.setFileSizeMax(3 * 1024 * 1024);// ??????????????????3MB
		servletFileUpload.setSizeMax(6 * 1024 * 1024);// ???????????????6MB
		List<FileItem> list = null;
		List<Object> params = new ArrayList<Object>();
		String DOB = "";
		params.add(UUIDTools.getUUID()); // ????????????id
		try {
			// ??????request??????
			list = servletFileUpload.parseRequest(request);
			// ??????????????????
			for (FileItem fileItem : list) {
				String fileItemName = fileItem.getFieldName();// ??????<input>???????????????
				String fileItemValue = fileItem.getString("utf-8");// ??????<input>????????????
				if (fileItemName.equals("name")) {
					params.add(fileItemValue); //??????
				} else if (fileItemName.equals("sex")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("year")) {
					if (!fileItemValue.isEmpty()) { // ??????????????????
						DOB += fileItemValue + "/"; // ????????????????????????
					}
				} else if (fileItemName.equals("month")) {
					if (!fileItemValue.isEmpty()) { // ??????????????????
						DOB += fileItemValue + "/"; // ????????????????????????
					}
				} else if (fileItemName.equals("day")) {
					if (fileItemValue.length() == 1) {
						fileItemValue = '0' + fileItemValue; //????????????
					} 
					DOB += fileItemValue;
					params.add(DOB); // ??????
				} else if (fileItemName.equals("Phone")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("Job")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("remark")) {
					params.add(fileItemValue);// ??????
				} 
			}

			// ????????????????????????
			boolean flag = service.addClient(params);
			if (flag) {
				response.sendRedirect(path + "/main.jsp");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// ??????????????????
	private void updateClient(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getContextPath();
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		servletFileUpload.setFileSizeMax(3 * 1024 * 1024);
		servletFileUpload.setSizeMax(6 * 1024 * 1024);
		String DOB = "";
		List<FileItem> list = null;
		List<Object> params = new ArrayList<Object>();
		try {
			// ??????request??????
			list = servletFileUpload.parseRequest(request);
			// ??????????????????
			for (FileItem fileItem : list) {
				String fileItemName = fileItem.getFieldName();// ??????<input>???????????????
				String fileItemValue = fileItem.getString("utf-8");// ??????<input>????????????
				if (fileItemName.equals("name")) {
					params.add(fileItemValue); //??????
				} else if (fileItemName.equals("sex")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("year")) {
					if (!fileItemValue.isEmpty()) { // ??????????????????
						DOB += fileItemValue + "/"; // ????????????????????????
					}
				} else if (fileItemName.equals("month")) {
					if (!fileItemValue.isEmpty()) { // ??????????????????
						DOB += fileItemValue + "/"; // ????????????????????????
					}
				} else if (fileItemName.equals("day")) {
					if (fileItemValue.length() == 1) {
						fileItemValue = '0' + fileItemValue; //????????????
					} 
					DOB += fileItemValue;
					params.add(DOB); // ??????
				} else if (fileItemName.equals("Phone")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("Job")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("remark")) {
					params.add(fileItemValue);// ??????
				} else if (fileItemName.equals("clientid")) {
					params.add(fileItemValue);
				}
			}			

			// ??????????????????????????????
			boolean flag = service.updateClient(params);
			if (flag) {
				response.sendRedirect(path + "/main.jsp");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		service = new ClientDao();
	}

}
