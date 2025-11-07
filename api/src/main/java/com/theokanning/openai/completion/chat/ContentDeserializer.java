package com.theokanning.openai.completion.chat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.theokanning.openai.assistants.message.content.AudioURL;
import com.theokanning.openai.assistants.message.content.ImageFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiangTao
 * @date 2024年04月10 11:17
 **/
public class ContentDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return jsonParser.getText();
        }
        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
            // 处理数组的情况
            List<MultiMediaContent> contents = new ArrayList<>();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                // 判断数组内元素类型并进行相应的反序列化
                MultiMediaContent content = parseContent(jsonParser);
                if (content != null) {
                    contents.add(content);
                }
            }
            return contents;
        }
        //抛出异常
        return null;
    }

    MultiMediaContent parseContent(JsonParser jsonParser) throws IOException {
        MultiMediaContent content = new MultiMediaContent();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();

            if ("type".equals(fieldName)) {
                content.setType(jsonParser.getText());
            } else if ("text".equals(fieldName)) {
                content.setText(jsonParser.getValueAsString());
            } else if ("image_url".equals(fieldName)) {
                content.setImageUrl(parseImageUrl(jsonParser));
            } else if ("image_file".equals(fieldName)) {
                content.setImageFile(parseImageFile(jsonParser));
            } else if ("input_audio".equals(fieldName)) {
                content.setInputAudio(parseInputAudio(jsonParser));
            } else if ("audio_url".equals(fieldName)) {
                content.setAudioUrl(parseAudioUrl(jsonParser));
            }
        }
        return content;
    }

    private ImageFile parseImageFile(JsonParser jsonParser) throws IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();

        // 关键修复1: 检查null值
        if (currentToken == JsonToken.VALUE_NULL) {
            return null;
        }

        // 关键修复2: 验证必须是对象开始
        if (currentToken != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT for image_file, but got: " + currentToken);
        }

        String fileId = null;
        String detail = null;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();

            if ("file_id".equals(fieldName)) {
                fileId = jsonParser.getValueAsString();
            } else if ("detail".equals(fieldName)) {
                detail = jsonParser.getValueAsString();
            }
        }
        return new ImageFile(fileId, detail);
    }

    private ImageUrl parseImageUrl(JsonParser jsonParser) throws IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();

        if (currentToken == JsonToken.VALUE_NULL) {
            return null;
        }

        if (currentToken != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT for image_url, but got: " + currentToken);
        }

        String url = null;
        String detail = null;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();

            if ("url".equals(fieldName)) {
                url = jsonParser.getValueAsString();
            } else if ("detail".equals(fieldName)) {
                detail = jsonParser.getValueAsString();
            }
        }
        return new ImageUrl(url, detail);
    }

    private InputAudio parseInputAudio(JsonParser jsonParser) throws IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();

        if (currentToken == JsonToken.VALUE_NULL) {
            return null;
        }

        if (currentToken != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT for input_audio, but got: " + currentToken);
        }

        String data = null;
        String format = null;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();

            if ("data".equals(fieldName)) {
                data = jsonParser.getValueAsString();
            } else if ("format".equals(fieldName)) {
                format = jsonParser.getValueAsString();
            }
        }
        return new InputAudio(data, format);
    }

    private AudioURL parseAudioUrl(JsonParser jsonParser) throws IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();

        if (currentToken == JsonToken.VALUE_NULL) {
            return null;
        }

        if (currentToken != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT for audio_url, but got: " + currentToken);
        }

        String url = null;
        String audioTranscript = null;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();

            if ("url".equals(fieldName)) {
                url = jsonParser.getValueAsString();
            } else if ("audio_transcript".equals(fieldName)) {
                audioTranscript = jsonParser.getValueAsString();
            }
        }
        return new AudioURL(url, audioTranscript);
    }
}
