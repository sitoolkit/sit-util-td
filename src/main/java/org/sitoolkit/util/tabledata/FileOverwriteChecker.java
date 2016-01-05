/*
 * Copyright 2014 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sitoolkit.util.tabledata;

import java.io.File;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、ファイルを上書き可否をユーザーに確認するためのクラスです。
 * 
 * @author yuichi.kuwahara
 */
public class FileOverwriteChecker {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final Object[] SELECTION_VALUES;

    private Writable allWritable = Writable.NA;

    private boolean rebuild;

    static {
        SELECTION_VALUES = new Object[] { Answer.y.getDescription(), Answer.a.getDescription(),
                Answer.n.getDescription(), Answer.q.getDescription() };

    }

    public boolean isWritable(File targetFile) {
        if (!targetFile.exists()) {
            return true;
        }

        if (rebuild) {
            return true;
        }

        if (Writable.No.equals(allWritable)) {
            return false;
        } else if (Writable.Yes.equals(allWritable)) {
            return true;
        }

        Writable writable = Writable.NA;
        while (writable == Writable.NA) {
            Object input = JOptionPane.showInputDialog(null,
                    "書込み先にファイルが存在します。\n" + targetFile.getAbsolutePath(), "SIToolkit ファイル上書き確認",
                    JOptionPane.INFORMATION_MESSAGE, null, SELECTION_VALUES,
                    Answer.n.getDescription());
            Answer answer = Answer.parseFromDescription(input);

            if (answer == null) {
                continue;
            }

            switch (answer) {
            case y:
                writable = Writable.Yes;
                break;
            case a:
                writable = Writable.Yes;
                allWritable = Writable.Yes;
                break;
            case n:
                writable = Writable.No;
                break;
            case q:
                writable = Writable.No;
                allWritable = Writable.No;
                break;
            default:
                writable = Writable.NA;
            }
        }
        return Writable.Yes.equals(writable);
    }

    enum Writable {
        Yes, No, NA
    }

    enum Answer {
        y("上書き"), a("以降全て上書き"), n("上書きしない"), q("以降全て上書きしない"),;
        private final String description;

        private Answer(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static Answer parseFromDescription(Object description) {
            for (Answer ans : values()) {
                if (ans.getDescription().equals(description)) {
                    return ans;
                }
            }
            return null;
        }

    }

    public boolean isRebuild() {
        return rebuild;
    }

    public void setRebuild(boolean rebuild) {
        this.rebuild = rebuild;
    }

}
