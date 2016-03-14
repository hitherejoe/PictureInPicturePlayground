package com.hitherejoe.androidtvboilerplate.test.common.rules;

import android.content.Context;

import com.hitherejoe.androidtvboilerplate.AndroidTvBoilerplateApplication;
import com.hitherejoe.androidtvboilerplate.data.DataManager;
import com.hitherejoe.androidtvboilerplate.test.common.injection.component.DaggerTestComponent;
import com.hitherejoe.androidtvboilerplate.test.common.injection.component.TestComponent;
import com.hitherejoe.androidtvboilerplate.test.common.injection.module.ApplicationTestModule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Test rule that creates and sets a Dagger TestComponent into the application overriding the
 * existing application component.
 * Use this rule in your test case in order for the app to use mock dependencies.
 * It also exposes some of the dependencies so they can be easily accessed from the tests, e.g. to
 * stub mocks etc.
 */
public class TestComponentRule implements TestRule {

    private final TestComponent mTestComponent;
    private final Context mContext;

    public TestComponentRule(Context context) {
        mContext = context;
        AndroidTvBoilerplateApplication application = AndroidTvBoilerplateApplication.get(context);
        mTestComponent = DaggerTestComponent.builder()
                .applicationTestModule(new ApplicationTestModule(application))
                .build();
    }

    public Context getContext() {
        return mContext;
    }

    public DataManager getMockDataManager() {
        return mTestComponent.dataManager();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                AndroidTvBoilerplateApplication application =
                        AndroidTvBoilerplateApplication.get(mContext);
                application.setComponent(mTestComponent);
                base.evaluate();
                application.setComponent(null);
            }
        };
    }
}