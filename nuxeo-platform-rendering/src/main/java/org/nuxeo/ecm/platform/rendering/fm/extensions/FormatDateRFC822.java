package org.nuxeo.ecm.platform.rendering.fm.extensions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;

public class FormatDateRFC822 extends FormatDate {

    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1) {
            throw new TemplateModelException(
                    "Invalid number of arguments for formatDate(Date date) method");
        }
        if (!(arguments.get(0) instanceof SimpleDate)) {
            throw new TemplateModelException(
                    "Invalid arguments format for the method formatDate : expecting (Date date, String local).");
        }

        SimpleDate simpledate = (SimpleDate) arguments.get(0);
        if (simpledate == null) {
            throw new TemplateModelException("the argument date is not defined");
        }

        Date date = simpledate.getAsDate();
        SimpleDateFormat sdFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
        return new SimpleScalar( sdFormat.format(date));
    }
    
}
