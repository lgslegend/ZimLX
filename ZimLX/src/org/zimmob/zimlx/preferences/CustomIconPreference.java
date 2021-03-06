/*
 * 2020 Zim Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.zimmob.zimlx.preferences;

import android.content.Context;
import android.util.AttributeSet;

import com.android.launcher3.R;

import java.util.HashMap;
import java.util.Map;

import androidx.preference.ListPreference;

import org.zimmob.zimlx.util.CustomIconUtils;

public class CustomIconPreference extends ListPreference {

    public CustomIconPreference(Context context) {
        super(context);
    }

    public CustomIconPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomIconPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomIconPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onClick() {
        reloadIconPacks();
        super.onClick();
    }

    public void reloadIconPacks() {
        Context context = getContext();
        HashMap<String, CharSequence> packList = CustomIconUtils.getPackProviders(context);

        CharSequence[] keys = new String[packList.size() + 1];
        keys[0] = context.getResources().getString(R.string.icon_shape_system_default);

        CharSequence[] values = new String[keys.length];
        values[0] = "";

        int i = 1;
        for (Map.Entry<String, CharSequence> entry : packList.entrySet()) {
            keys[i] = entry.getValue();
            values[i++] = entry.getKey();
        }

        setEntries(keys);
        setEntryValues(values);
    }
}