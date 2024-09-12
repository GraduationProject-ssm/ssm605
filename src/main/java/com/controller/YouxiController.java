
package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.YouxiEntity;
import com.entity.view.YouxiView;
import com.service.DictionaryService;
import com.service.TokenService;
import com.service.YonghuService;
import com.service.YouxiService;
import com.utils.PageUtils;
import com.utils.PoiUtil;
import com.utils.R;
import com.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 游戏
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/youxi")
public class YouxiController {
    private static final Logger logger = LoggerFactory.getLogger(YouxiController.class);

    @Autowired
    private YouxiService youxiService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service

    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        params.put("youxiDeleteStart",1);params.put("youxiDeleteEnd",1);
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = youxiService.queryPage(params);

        //字典表数据转换
        List<YouxiView> list =(List<YouxiView>)page.getList();
        for(YouxiView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        YouxiEntity youxi = youxiService.selectById(id);
        if(youxi !=null){
            //entity转view
            YouxiView view = new YouxiView();
            BeanUtils.copyProperties( youxi , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody YouxiEntity youxi, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,youxi:{}",this.getClass().getName(),youxi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<YouxiEntity> queryWrapper = new EntityWrapper<YouxiEntity>()
            .eq("youxi_name", youxi.getYouxiName())
            .eq("youxi_uuid_number", youxi.getYouxiUuidNumber())
            .eq("youxi_types", youxi.getYouxiTypes())
            .eq("youxi_zuidipeizhi", youxi.getYouxiZuidipeizhi())
            .eq("youxi_tuijianpeizhi", youxi.getYouxiTuijianpeizhi())
            .eq("youxi_kaifashang", youxi.getYouxiKaifashang())
            .eq("youxi_yuyan", youxi.getYouxiYuyan())
            .eq("youxi_kongjian", youxi.getYouxiKongjian())
            .eq("youxi_shoufa", youxi.getYouxiShoufa())
            .eq("youxi_address", youxi.getYouxiAddress())
            .eq("youxi_price", youxi.getYouxiPrice())
            .eq("youxi_clicknum", youxi.getYouxiClicknum())
            .eq("shangxia_types", youxi.getShangxiaTypes())
            .eq("youxi_delete", youxi.getYouxiDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YouxiEntity youxiEntity = youxiService.selectOne(queryWrapper);
        if(youxiEntity==null){
            youxi.setYouxiClicknum(1);
            youxi.setShangxiaTypes(1);
            youxi.setYouxiDelete(1);
            youxi.setCreateTime(new Date());
            youxiService.insert(youxi);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody YouxiEntity youxi, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,youxi:{}",this.getClass().getName(),youxi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        //根据字段查询是否有相同数据
        Wrapper<YouxiEntity> queryWrapper = new EntityWrapper<YouxiEntity>()
            .notIn("id",youxi.getId())
            .andNew()
            .eq("youxi_name", youxi.getYouxiName())
            .eq("youxi_uuid_number", youxi.getYouxiUuidNumber())
            .eq("youxi_types", youxi.getYouxiTypes())
            .eq("youxi_zuidipeizhi", youxi.getYouxiZuidipeizhi())
            .eq("youxi_tuijianpeizhi", youxi.getYouxiTuijianpeizhi())
            .eq("youxi_kaifashang", youxi.getYouxiKaifashang())
            .eq("youxi_yuyan", youxi.getYouxiYuyan())
            .eq("youxi_kongjian", youxi.getYouxiKongjian())
            .eq("youxi_shoufa", youxi.getYouxiShoufa())
            .eq("youxi_address", youxi.getYouxiAddress())
            .eq("youxi_price", youxi.getYouxiPrice())
            .eq("youxi_clicknum", youxi.getYouxiClicknum())
            .eq("shangxia_types", youxi.getShangxiaTypes())
            .eq("youxi_delete", youxi.getYouxiDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YouxiEntity youxiEntity = youxiService.selectOne(queryWrapper);
        if("".equals(youxi.getYouxiPhoto()) || "null".equals(youxi.getYouxiPhoto())){
                youxi.setYouxiPhoto(null);
        }
        if(youxiEntity==null){
            youxiService.updateById(youxi);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        ArrayList<YouxiEntity> list = new ArrayList<>();
        for(Integer id:ids){
            YouxiEntity youxiEntity = new YouxiEntity();
            youxiEntity.setId(id);
            youxiEntity.setYouxiDelete(2);
            list.add(youxiEntity);
        }
        if(list != null && list.size() >0){
            youxiService.updateBatchById(list);
        }
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save(String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<YouxiEntity> youxiList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("../../upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            YouxiEntity youxiEntity = new YouxiEntity();
//                            youxiEntity.setYouxiName(data.get(0));                    //游戏名称 要改的
//                            youxiEntity.setYouxiUuidNumber(data.get(0));                    //游戏编号 要改的
//                            youxiEntity.setYouxiPhoto("");//详情和图片
//                            youxiEntity.setYouxiTypes(Integer.valueOf(data.get(0)));   //游戏类型 要改的
//                            youxiEntity.setYouxiZuidipeizhi(data.get(0));                    //最低配置 要改的
//                            youxiEntity.setYouxiTuijianpeizhi(data.get(0));                    //推荐配置 要改的
//                            youxiEntity.setYouxiKaifashang(data.get(0));                    //开发商 要改的
//                            youxiEntity.setYouxiYuyan(data.get(0));                    //支持语言 要改的
//                            youxiEntity.setYouxiKongjian(data.get(0));                    //需要空间 要改的
//                            youxiEntity.setYouxiShoufa(data.get(0));                    //首发日期 要改的
//                            youxiEntity.setYouxiAddress(data.get(0));                    //游戏下载链接 要改的
//                            youxiEntity.setYouxiPrice(Integer.valueOf(data.get(0)));   //购买获得积分 要改的
//                            youxiEntity.setYouxiOldMoney(data.get(0));                    //游戏原价 要改的
//                            youxiEntity.setYouxiNewMoney(data.get(0));                    //现价 要改的
//                            youxiEntity.setYouxiClicknum(Integer.valueOf(data.get(0)));   //游戏热度 要改的
//                            youxiEntity.setYouxiContent("");//详情和图片
//                            youxiEntity.setShangxiaTypes(Integer.valueOf(data.get(0)));   //是否上架 要改的
//                            youxiEntity.setYouxiDelete(1);//逻辑删除字段
//                            youxiEntity.setCreateTime(date);//时间
                            youxiList.add(youxiEntity);


                            //把要查询是否重复的字段放入map中
                                //游戏编号
                                if(seachFields.containsKey("youxiUuidNumber")){
                                    List<String> youxiUuidNumber = seachFields.get("youxiUuidNumber");
                                    youxiUuidNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> youxiUuidNumber = new ArrayList<>();
                                    youxiUuidNumber.add(data.get(0));//要改的
                                    seachFields.put("youxiUuidNumber",youxiUuidNumber);
                                }
                        }

                        //查询是否重复
                         //游戏编号
                        List<YouxiEntity> youxiEntities_youxiUuidNumber = youxiService.selectList(new EntityWrapper<YouxiEntity>().in("youxi_uuid_number", seachFields.get("youxiUuidNumber")).eq("youxi_delete", 1));
                        if(youxiEntities_youxiUuidNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(YouxiEntity s:youxiEntities_youxiUuidNumber){
                                repeatFields.add(s.getYouxiUuidNumber());
                            }
                            return R.error(511,"数据库的该表中的 [游戏编号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        youxiService.insertBatch(youxiList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }





    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = youxiService.queryPage(params);

        //字典表数据转换
        List<YouxiView> list =(List<YouxiView>)page.getList();
        for(YouxiView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        YouxiEntity youxi = youxiService.selectById(id);
            if(youxi !=null){

                //点击数量加1
                youxi.setYouxiClicknum(youxi.getYouxiClicknum()+1);
                youxiService.updateById(youxi);

                //entity转view
                YouxiView view = new YouxiView();
                BeanUtils.copyProperties( youxi , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody YouxiEntity youxi, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,youxi:{}",this.getClass().getName(),youxi.toString());
        Wrapper<YouxiEntity> queryWrapper = new EntityWrapper<YouxiEntity>()
            .eq("youxi_name", youxi.getYouxiName())
            .eq("youxi_uuid_number", youxi.getYouxiUuidNumber())
            .eq("youxi_types", youxi.getYouxiTypes())
            .eq("youxi_zuidipeizhi", youxi.getYouxiZuidipeizhi())
            .eq("youxi_tuijianpeizhi", youxi.getYouxiTuijianpeizhi())
            .eq("youxi_kaifashang", youxi.getYouxiKaifashang())
            .eq("youxi_yuyan", youxi.getYouxiYuyan())
            .eq("youxi_kongjian", youxi.getYouxiKongjian())
            .eq("youxi_shoufa", youxi.getYouxiShoufa())
            .eq("youxi_address", youxi.getYouxiAddress())
            .eq("youxi_price", youxi.getYouxiPrice())
            .eq("youxi_clicknum", youxi.getYouxiClicknum())
            .eq("shangxia_types", youxi.getShangxiaTypes())
            .eq("youxi_delete", youxi.getYouxiDelete())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YouxiEntity youxiEntity = youxiService.selectOne(queryWrapper);
        if(youxiEntity==null){
            youxi.setYouxiDelete(1);
            youxi.setCreateTime(new Date());
        youxiService.insert(youxi);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


}