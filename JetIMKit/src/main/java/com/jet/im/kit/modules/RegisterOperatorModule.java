package com.jet.im.kit.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;

import com.jet.im.kit.R;
import com.jet.im.kit.SendbirdUIKit;
import com.jet.im.kit.modules.components.RegisterOperatorListComponent;
import com.jet.im.kit.modules.components.SelectUserHeaderComponent;
import com.jet.im.kit.modules.components.StatusComponent;

/**
 * A module for registering operators. This module is composed of a header, list, and status.
 * All composed components are created when the module is created. After than those components can replace.
 *
 * <ul>
 *  <li>Header component is {@link SelectUserHeaderComponent} and you can set custom header component by {@link #setHeaderComponent(SelectUserHeaderComponent)}
 *  <li>List component is {@link RegisterOperatorListComponent} and you can set custom list component by {@link #setRegisterOperatorListComponent(RegisterOperatorListComponent)}
 *  <li>Status component is {@link StatusComponent} and you can set custom status component by {@link #setStatusComponent(StatusComponent)}
 * </ul>
 *
 * since 3.0.0
 */
public class RegisterOperatorModule extends BaseModule {
    @NonNull
    private final Params params;
    @NonNull
    private SelectUserHeaderComponent headerComponent;
    @NonNull
    private RegisterOperatorListComponent registerOperatorListComponent;
    @NonNull
    private StatusComponent statusComponent;

    /**
     * Constructor
     *
     * @param context The {@code Context} this module is currently associated with
     * since 3.0.0
     */
    public RegisterOperatorModule(@NonNull Context context) {
        this(context, new Params(context));
    }

    /**
     * Constructor
     *
     * @param context The {@code Context} this module is currently associated with
     * @param params The Parameter object that can customize a default Fragment.
     * since 3.0.0
     */
    public RegisterOperatorModule(@NonNull Context context, @NonNull Params params) {
        this.params = params;
        this.headerComponent = new SelectUserHeaderComponent();
        this.headerComponent.getParams().setRightButtonText(context.getString(R.string.sb_text_button_add));
        this.registerOperatorListComponent = new RegisterOperatorListComponent();
        this.statusComponent = new StatusComponent();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle args) {
        if (args != null) params.apply(context, args);
        final Context moduleContext = new ContextThemeWrapper(context, params.getTheme());
        final LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parent.setOrientation(LinearLayout.VERTICAL);

        final TypedValue values = new TypedValue();
        if (params.shouldUseHeader()) {
            moduleContext.getTheme().resolveAttribute(R.attr.sb_component_state_header, values, true);
            final Context headerThemeContext = new ContextThemeWrapper(moduleContext, values.resourceId);
            final LayoutInflater headerInflater = inflater.cloneInContext(headerThemeContext);
            final View header = this.headerComponent.onCreateView(headerThemeContext, headerInflater, parent, args);
            parent.addView(header);
        }

        final FrameLayout innerContainer = new FrameLayout(context);
        innerContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parent.addView(innerContainer);

        moduleContext.getTheme().resolveAttribute(R.attr.sb_component_list, values, true);
        final Context listThemeContext = new ContextThemeWrapper(moduleContext, values.resourceId);
        final LayoutInflater listInflater = inflater.cloneInContext(listThemeContext);
        final View memberListLayout = registerOperatorListComponent.onCreateView(listThemeContext, listInflater, innerContainer, args);
        innerContainer.addView(memberListLayout);

        moduleContext.getTheme().resolveAttribute(R.attr.sb_component_status, values, true);
        final Context statusThemeContext = new ContextThemeWrapper(moduleContext, values.resourceId);
        final LayoutInflater statusInflater = inflater.cloneInContext(statusThemeContext);
        final View statusLayout = statusComponent.onCreateView(statusThemeContext, statusInflater, innerContainer, args);
        innerContainer.addView(statusLayout);
        return parent;
    }

    /**
     * Sets a custom header component.
     *
     * @param component The header component to be used in this module
     * since 3.0.0
     */
    public <T extends SelectUserHeaderComponent> void setHeaderComponent(@NonNull T component) {
        this.headerComponent = component;
    }

    /**
     * Sets a custom list component.
     *
     * @param component The list component to be used in this module
     * since 3.0.0
     */
    public <T extends RegisterOperatorListComponent> void setRegisterOperatorListComponent(@NonNull T component) {
        this.registerOperatorListComponent = component;
    }

    /**
     * Sets a custom status component.
     *
     * @param component The status component to be used in this module
     * since 3.0.0
     */
    public <T extends StatusComponent> void setStatusComponent(@NonNull T component) {
        this.statusComponent = component;
    }

    /**
     * Returns the header component.
     *
     * @return The header component of this module
     * since 3.0.0
     */
    @NonNull
    public SelectUserHeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    /**
     * Returns the list component.
     *
     * @return The list component of this module
     * since 3.0.0
     */
    @NonNull
    public RegisterOperatorListComponent getRegisterOperatorListComponent() {
        return registerOperatorListComponent;
    }

    /**
     * Returns the status component.
     *
     * @return The status component of this module
     * since 3.0.0
     */
    @NonNull
    public StatusComponent getStatusComponent() {
        return statusComponent;
    }

    /**
     * Returns a collection of parameters applied to this module.
     *
     * @return {@link Params} applied to this module.
     * since 3.0.0
     */
    @NonNull
    public Params getParams() {
        return params;
    }

    public static class Params extends BaseModule.Params {

        /**
         * Constructor
         *
         * @param context The {@code Context} this module is currently associated with
         * since 3.0.0
         */
        public Params(@NonNull Context context) {
            this(context, SendbirdUIKit.getDefaultThemeMode());
        }

        /**
         * Constructor
         *
         * @param context The {@code Context} this module is currently associated with
         * @param themeMode The theme of Sendbird UIKit to be applied to this module
         * since 3.0.0
         */
        public Params(@NonNull Context context, @NonNull SendbirdUIKit.ThemeMode themeMode) {
            super(context, themeMode, R.attr.sb_module_register_operator_list);
        }

        /**
         * Constructor
         *
         * @param context The {@code Context} this module is currently associated with
         * @param themeResId The theme resource ID to be applied to this module
         * since 3.0.0
         */
        public Params(@NonNull Context context, @StyleRes int themeResId) {
            super(context, themeResId, R.attr.sb_module_register_operator_list);
        }
    }
}
