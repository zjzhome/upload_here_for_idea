## Upload Here WebStorm Plugin

## Features

1. 使用快捷键（默认 shift + alt + p），上传本地图片然后将上传结果插入到光标位置。
2. markdown 文件支持插入markdown 图片语法，对于写文档很便利。

## Extension Settings

使用该插件，首先需要配置图片上传接口地址：

`Preference -> Tools -> Upload Here`

该接口返回值需要满足如下格式：

```json
{
    "data": {
        "url": "https://xxxx.png"
    }
}
```

