/**
 * Copyright (c) 2013 HAN University of Applied Sciences
 * Arjan Oortgiese
 * Joëll Portier
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package nl.han.dare2date.service.web;

import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.service.jms.util.JMSUtil;
import nl.han.dare2date.service.jms.util.Queues;
import nl.han.dare2date.service.jms.util.Requestor;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import java.io.Serializable;

/**
 * 
 * @author mdkr
 *
 * Is used as a JMS client using request-reply
 */
public class ValidateCreditcardService extends Requestor implements IValidateCreditcardService {

    private Creditcard cc;

    public ValidateCreditcardService() throws JMSException, NamingException{
        super(JMSUtil.getConnection(), Queues.REQUEST_QUEUE, Queues.REPLY_QUEUE, Queues.INVALID_QUEUEU);
    }

	public boolean validate(Creditcard cc) {
        boolean isValid = false;
        this.cc = cc;
        try {
            this.send();
            this.receiveSync();
            isValid = getReplyMessage().getBooleanProperty("valid");
        } catch(JMSException e) {
            e.printStackTrace();
        }
        System.out.println("VALIDATION");
        System.out.println(isValid);

        return isValid;
	}

    @Override
    public ObjectMessage getObjectMessage() {
        ObjectMessage msg = null;
        try{
            msg = getSession().createObjectMessage(cc);}
        catch(JMSException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public Serializable getResponse() {
        return null;
    }
}