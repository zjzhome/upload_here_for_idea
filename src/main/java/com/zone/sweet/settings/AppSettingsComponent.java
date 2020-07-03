package com.zone.sweet.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a JPanel for the Settings Dialog.
 */
public class AppSettingsComponent {
    private final JPanel UploadHereSettingPanel;
    private final JBTextField UploadUrlText = new JBTextField();

    public AppSettingsComponent() {
        UploadHereSettingPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter Upload Url: "), UploadUrlText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return UploadHereSettingPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return UploadUrlText;
    }

    @NotNull
    public String getUserNameText() {
        return UploadUrlText.getText();
    }

    public void setUserNameText(@NotNull String newText) {
        UploadUrlText.setText(newText);
    }
}
