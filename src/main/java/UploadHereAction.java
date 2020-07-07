import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.zone.sweet.settings.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UploadHereAction extends AnAction {

    private VirtualFile getChooseFileLocalPath(Project project) {
        // 选择图片文件
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        final VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, project, null);
        if (virtualFile == null) {
            return null;
        }
        if(virtualFile.isDirectory() || !virtualFile.isInLocalFileSystem()) {
            return null;
        }
        String ext = virtualFile.getExtension();
        if(ext == null) {
            return null;
        }
        System.out.println("本地路径: " + virtualFile.getPath());
        if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif") || ext.equals("icon") || ext.equals("svg")) {
            return virtualFile;
        }
        return null;
    }

    private String uploadImage(String uploadUrl, String filePath) {
        //Creating CloseableHttpClient object
        CloseableHttpClient httpClient = HttpClients.createDefault();

        File file = new File(filePath);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addBinaryBody("file", file);
        HttpEntity multiPartHttpEntity = entityBuilder.build();

        RequestBuilder requestBuilder = RequestBuilder.post(uploadUrl);
        requestBuilder.setEntity(multiPartHttpEntity);
        HttpUriRequest multipartRequest = requestBuilder.build();

        HttpResponse response = null;
        try {
            response = httpClient.execute(multipartRequest);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if(response == null) {
            Messages.showErrorDialog("上传失败，请重试", "Upload Here");
            return null;
        }
        HttpEntity responseEntity = response.getEntity();
        String sResponse = null;
        try {
            sResponse = EntityUtils.toString(responseEntity, "UTF-8");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if(sResponse == null) {
            Messages.showErrorDialog("上传失败，请重试", "Upload Here");
            return null;
        }
        System.out.println("上传结果 " + sResponse);

        JSONObject jsonObject = new JSONObject(sResponse);
        return (String) jsonObject.getJSONObject("data").get("url");
    }

    private void replaceStringInDocument(Project project, Document document, Editor editor, String str) {
        if(str == null) {
            return;
        }
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, str)
        );

        primaryCaret.removeSelection();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getData(PlatformDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if(editor == null) {
            return;
        }
        final Document document = editor.getDocument();
        final PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if(psiFile == null) {
            return;
        }
        Language language = psiFile.getLanguage();
        String languageType = language.getID().toLowerCase();

        // 获取配置
        AppSettingsState settings = AppSettingsState.getInstance();
        String uploadUrl = settings.uploadUrl;

        if(uploadUrl == null) {
            Messages.showErrorDialog("请先配置图片上传接口地址", "Upload Here");
            return;
        }

        // 选择文件
        VirtualFile virtualFile = getChooseFileLocalPath(project);
        if(virtualFile == null) {
            return;
        }

        // 上传
        String imageUrl = uploadImage(uploadUrl, virtualFile.getPath());

        // md 特殊处理
        String insertString = imageUrl;
        if(languageType.equals("markdown")) {
            insertString = "![" + virtualFile.getName() + "](" + imageUrl + ")";
        }

        // 插入文档
        replaceStringInDocument(project, document, editor, insertString);
    }
}
