package cn.nome.saas.search.component;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * @author chentaikuang
 */
public class BufferServletReqWrap extends HttpServletRequestWrapper {
    private final byte[] buffer;

    public byte[] getBuffer() {
        return buffer;
    }

    public BufferServletReqWrap(HttpServletRequest request) throws IOException {
        super(request);
        this.buffer = IOUtils.toByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new WrapServletInputStream(this.buffer);
    }
}