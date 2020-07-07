package com.zone.sweet.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {
    private AppSettingsComponent uploadHereSettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Upload Here";
    }

    public JComponent getPreferredFocusedComponent() {
        return uploadHereSettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        uploadHereSettingsComponent = new AppSettingsComponent();
        return uploadHereSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        return !uploadHereSettingsComponent.getUserNameText().equals(settings.uploadUrl);
    }

    @Override
    public void apply() throws ConfigurationException {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.uploadUrl = uploadHereSettingsComponent.getUserNameText();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        uploadHereSettingsComponent.setUserNameText(settings.uploadUrl);
    }

    @Override
    public void disposeUIResources() {
        uploadHereSettingsComponent = null;
    }

}
