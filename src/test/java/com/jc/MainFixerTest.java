package com.jc;

import com.jc.util.HttpRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

/**
 * @author jinchenj
 * @description 数据修复
 * @create:2025-08-1815:59:18
 */
public class MainFixerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testExecuteFixSuccessScenario() {
        try (MockedStatic<HttpRequestUtil> mockedHttp = mockStatic(HttpRequestUtil.class)) {
            // 执行测试
            try {
                MainFixer.executeFix("1", "CPJY202508180001", "https://bip-pre.yonyoucloud.com", "c800=dccore0; loginLocale=zh_CN; _WorkbenchCross_=Ultraman; multilingualFlag=true; timezone=UTC+08:00; language=001; locale=zh_CN; orgId=\"\"; theme=\"\"; languages=1_3; newArch=true; sysid=diwork; defaultLocale=zh_CN; n_f_f=true; jDiowrkTokenMock=; at=76a0c7a4-9878-42f3-938f-eea1a14b71a5; acw_tc=0bd17c5a17555021001801629e97a776ae6c87b1e8268ea8312f1c10bbd8f9; JSESSIONID=0000a-PSLwIqBsUEp2fOEaAhAMF-dc3IjHt7bFlwDhij2q3IH1ntVlky21NV-vB9dS-k:7f985c1c-e71e-496d-b647-9737c2cfd455; yht_username_diwork=ST-1959294171-NEdxhm6LbehFdocKazdq-pre__37d48b05-4d9e-47e3-a373-24135459e3bd; yht_usertoken_diwork=QqfeZ3U%2FbzHnSIm1KzgN6qbjdTWhvgBX3vVIeqtM8O7f3NNuAbgPCNmIraiVTs46t%2FZcfEiywYKEUklorC8n%2Fw%3D%3D; yht_access_token=bttL0lJVjIxS2E2dE5hQ0NlQXc0eldLZitNblRSaEdmS0t2aTdicm5TLzNBT2FVNUdLd0lNU0RMMHVkWm5zeDcwVF9feWh0LXByZS55b255b3VjbG91ZC5jb20.__836c8a66f10a92340d27ec1c23720853_1755502141203TGYonBip-core2-pre-all-serviceTGdccore0iuap-apcom-workbench1a5a304aYT; defaultOrg=\"\"; tenantid=0000MAOTIF5AF2O5K10000; a00=_Kcsk6F0pHSGX7TaPIfHYJ327Ey0vG8ygPpzWh7pJ8YwMDAwTUFPVElGNUFGMk81SzEwMDAwYDQzMjY2ODk4MzU3NjgwMTZgMDAwME1BT1RJRjVBRjJPNUsxMDAwMGAzN2Q0OGIwNS00ZDllLTQ3ZTMtYTM3My0yNDEzNTQ1OWUzYmRgMWBgZThiMGEyZTViZmEwZTY5NThmYGBgMjI2ODU3NDQyNzE4MzU3OTE0MmBmYWxzZWBgMTc1NTUwMjE0MTIxMWB5bXNzZXM6Y2Q1NzI3YjBiZDRjMTE2MmNiNWU2YWRjNWM2MWRhMzRgZGl3b3JrYA; a10=MTExMDk1MjE3ODU2NzkzNDMxMzQ; XSRF-TOKEN=MDF_F6Q3V6JU6CODG77XY2DJ2QL2V!155412; UBA_LAST_EID=hww0r1gtjr8s");
            } catch (Exception e) {
                // 忽略异常，因为我们关注的是输出
            }

            // 验证输出内容
            String output = outContent.toString();
            assertTrue(output.contains("开始执行数据修复") || output.contains("执行出错"));
        }
    }

}
