/**  
 * @Title: GlobalSetting.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月10日 下午3:54:10
 * @version 
 */
package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

/**  
 * @Title: GlobalSettingController.java
 * @Package ampc.com.gistone.controller
 * @Description: 设置全局参数 关于实时预报的设置
 * @author yanglei
 * @date 2017年4月10日 下午3:54:10
 * @version 1.0
 */
@RestController
@RequestMapping
public class GlobalSettingController {
	
	//加载全局参数的映射
	/*@Autowired
	private TGlobalSettingMapper tGlobalSettingMapper;
	
	
	
	
	@RequestMapping("/GlobalSetting")
	public AmpcResult getRunModel(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		String token = (String) requestDate.get("token");
			//获取数据包
			try {
				ClientUtil.SetCharsetAndHeader(request, response);
				Map<String,Object> data=(Map)requestDate.get("data");
				
				//用户id
				Long userId = Long.parseLong(data.get("userId").toString());
				//预报天数间隔
				Integer rangeDay = Integer.parseInt(data.get("rangeDay").toString());
				//spinup
				Integer spinup = Integer.parseInt(data.get("spinup").toString());
				//计算核数（实时预报）
				Integer cores = Integer.parseInt(data.get("cores").toString());
				//domainID
				Long domainId = Long.parseLong(data.get("domainId").toString());
				//清单id
				Long esCouplingId = Long.parseLong(data.get("esCouplingId").toString());
				
				TGlobalSetting tGlobalSetting = new TGlobalSetting();
				//添加时间
				tGlobalSetting.setAddDate(new Date());
				//用户ID
				tGlobalSetting.setUserid(userId);
				//实时预报天数
				tGlobalSetting.setRangeday(rangeDay);
				//spinup
				tGlobalSetting.setSpinup(spinup);
				//计算核数
				tGlobalSetting.setCores(cores);
				//domainid
				tGlobalSetting.setDomainId(domainId);
				//清单ID
				tGlobalSetting.setEsCouplingId(esCouplingId);
				int insert = tGlobalSettingMapper.insertSelective(tGlobalSetting);
				if (insert>0) {
					return AmpcResult.build(0, "success");
				}else {
					return AmpcResult.build(1000, "参数错误");
				}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return AmpcResult.build(1000, "参数失败");
			}
	}
*/
}
