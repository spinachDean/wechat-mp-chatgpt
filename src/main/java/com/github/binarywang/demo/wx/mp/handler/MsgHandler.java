package com.github.binarywang.demo.wx.mp.handler;

import com.github.binarywang.demo.wx.mp.builder.TextBuilder;
import com.github.binarywang.demo.wx.mp.utils.OkHttpUtils;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.common.Choice;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class MsgHandler extends AbstractHandler {

    private OpenAiClient openAiClient;

    @PostConstruct
    public void init() {
        openAiClient = OpenAiClient.builder()
                .apiHost("https://openai.nooc.ink/")
                .apiKey("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")
                .build();
    }


    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        logger.info("新消息接收: content = " + wxMessage.getContent());
        CompletionResponse completions = openAiClient.completions(wxMessage.getContent());
        logger.info("新消息接收: completions = " + completions);
        Choice choice = completions.getChoices()[0];
        String text = choice.getText();
        if(StringUtils.isNotEmpty(text)){
            return new TextBuilder().build(text, wxMessage, weixinService);
        }
        return new TextBuilder().build("目前没有处理回复的策略哦", wxMessage, weixinService);
    }
}
