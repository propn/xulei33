package com.propn.golf.mvc.multipart;

import java.io.*;

class DefaultFileRenamePolicy implements FileRenamePolicy {

    @Override
    public File rename(File f) {
        if (createNewFile(f)) {
            return f;
        }
        String name = f.getName();
        String body = null;
        String ext = null;

        int dot = name.lastIndexOf(".");
        if (dot != -1) {
            body = name.substring(0, dot);
            ext = name.substring(dot); // includes "."
        } else {
            body = name;
            ext = "";
        }
        int count = 0;
        while (!createNewFile(f) && count < 9999) {
            count++;
            String newName = body + count + ext;
            f = new File(f.getParent(), newName);
        }

        return f;
    }

    private boolean createNewFile(File f) {
        try {
            return f.createNewFile();
        } catch (IOException ignored) {
            return false;
        }
    }
}
