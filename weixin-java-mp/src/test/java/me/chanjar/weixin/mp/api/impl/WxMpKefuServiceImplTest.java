package me.chanjar.weixin.mp.api.impl;

import java.io.File;
import java.util.Date;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.ApiTestModule;
import me.chanjar.weixin.mp.api.ApiTestModule.WxXmlMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.bean.kefu.request.WxMpKfAccountRequest;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfInfo;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfList;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfMsgList;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfOnlineList;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfSessionGetResult;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfSessionList;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfSessionWaitCaseList;

/**
 * 测试客服相关接口
 * @author Binary Wang
 *
 */
@Test
@Guice(modules = ApiTestModule.class)
public class WxMpKefuServiceImplTest {

  @Inject
  protected WxMpServiceImpl wxService;

  public void testKfList() throws WxErrorException {
    WxMpKfList kfList = this.wxService.getKefuService().kfList();
    Assert.assertNotNull(kfList);
    for (WxMpKfInfo k : kfList.getKfList()) {
      System.err.println(k);
    }
  }

  public void testKfOnlineList() throws WxErrorException {
    WxMpKfOnlineList kfOnlineList = this.wxService.getKefuService()
        .kfOnlineList();
    Assert.assertNotNull(kfOnlineList);
    for (WxMpKfInfo k : kfOnlineList.getKfOnlineList()) {
      System.err.println(k);
    }
  }

  @DataProvider
  public Object[][] getKfAccount() {
    WxXmlMpInMemoryConfigStorage configStorage = (WxXmlMpInMemoryConfigStorage) this.wxService
        .getWxMpConfigStorage();
    return new Object[][] { { configStorage.getKfAccount() } };
  }

  @Test(dataProvider = "getKfAccount")
  public void testKfAccountAdd(String kfAccount) throws WxErrorException {
    WxMpKfAccountRequest request = WxMpKfAccountRequest.builder()
        .kfAccount(kfAccount).nickName("我晕").build();
    Assert.assertTrue(this.wxService.getKefuService().kfAccountAdd(request));
  }

  @Test(dependsOnMethods = {
      "testKfAccountAdd" }, dataProvider = "getKfAccount")
  public void testKfAccountUpdate(String kfAccount) throws WxErrorException {
    WxMpKfAccountRequest request = WxMpKfAccountRequest.builder()
        .kfAccount(kfAccount).nickName("我晕").build();
    Assert.assertTrue(this.wxService.getKefuService().kfAccountUpdate(request));
  }

  @Test(dependsOnMethods = {
          "testKfAccountAdd" }, dataProvider = "getKfAccount")
  public void testKfAccountInviteWorker(String kfAccount) throws WxErrorException {
    WxMpKfAccountRequest request = WxMpKfAccountRequest.builder()
            .kfAccount(kfAccount).inviteWx("www_ucredit_com").build();
    Assert.assertTrue(this.wxService.getKefuService().kfAccountInviteWorker(request));
  }

  @Test(dependsOnMethods = {
      "testKfAccountUpdate" }, dataProvider = "getKfAccount")
  public void testKfAccountUploadHeadImg(String kfAccount)
      throws WxErrorException {
    File imgFile = new File("src\\test\\resources\\mm.jpeg");
    boolean result = this.wxService.getKefuService()
        .kfAccountUploadHeadImg(kfAccount, imgFile);
    Assert.assertTrue(result);
  }

  @Test(dataProvider = "getKfAccount")
  public void testKfAccountDel(String kfAccount) throws WxErrorException {
    boolean result = this.wxService.getKefuService().kfAccountDel(kfAccount);
    Assert.assertTrue(result);
  }

  @DataProvider
  public Object[][] getKfAccountAndOpenid() {
    WxXmlMpInMemoryConfigStorage configStorage = (WxXmlMpInMemoryConfigStorage) this.wxService
        .getWxMpConfigStorage();
    return new Object[][] {
        { configStorage.getKfAccount(), configStorage.getOpenId() } };
  }

  @Test(dataProvider = "getKfAccountAndOpenid")
  public void testKfSessionCreate(String kfAccount, String openid)
      throws WxErrorException {
    boolean result = this.wxService.getKefuService().kfSessionCreate(openid,
        kfAccount);
    Assert.assertTrue(result);
  }

  @Test(dataProvider = "getKfAccountAndOpenid")
  public void testKfSessionClose(String kfAccount, String openid)
      throws WxErrorException {
    boolean result = this.wxService.getKefuService().kfSessionClose(openid,
        kfAccount);
    Assert.assertTrue(result);
  }

  @Test(dataProvider = "getKfAccountAndOpenid")
  public void testKfSessionGet(String kfAccount,
      String openid) throws WxErrorException {
    WxMpKfSessionGetResult result = this.wxService.getKefuService()
        .kfSessionGet(openid);
    Assert.assertNotNull(result);
    System.err.println(result);
  }

  @Test(dataProvider = "getKfAccount")
  public void testKfSessionList(String kfAccount) throws WxErrorException {
    WxMpKfSessionList result = this.wxService.getKefuService()
        .kfSessionList(kfAccount);
    Assert.assertNotNull(result);
    System.err.println(result);
  }

  @Test
  public void testKfSessionGetWaitCase() throws WxErrorException {
    WxMpKfSessionWaitCaseList result = this.wxService.getKefuService()
        .kfSessionGetWaitCase();
    Assert.assertNotNull(result);
    System.err.println(result);
  }

  @Test
  public void testKfMsgList() throws WxErrorException, JsonProcessingException {
    Date startTime = DateTime.now().minusDays(1).toDate();
    Date endTime = DateTime.now().minusDays(0).toDate();
    WxMpKfMsgList result = this.wxService.getKefuService().kfMsgList(startTime,endTime, 1L, 50);
    Assert.assertNotNull(result);
    System.err.println(new ObjectMapper().writeValueAsString(result));
  }

  @Test
  public void testKfMsgListAll() throws WxErrorException, JsonProcessingException {
    Date startTime = DateTime.now().minusDays(1).toDate();
    Date endTime = DateTime.now().minusDays(0).toDate();
    WxMpKfMsgList result = this.wxService.getKefuService().kfMsgList(startTime,endTime);
    Assert.assertNotNull(result);
    System.err.println(new ObjectMapper().writeValueAsString(result));
  }
}
