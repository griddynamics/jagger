/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.master;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import com.griddynamics.jagger.exception.TechnicalException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class HibernateSessionIdProvider extends HibernateDaoSupport implements SessionIdProvider {
	private String sessionId;
    private String sessionName;
    private String sessionComment;

	@Override
	public String getSessionId() {
		if (sessionId == null) {
			synchronized (this) {
				if (sessionId == null) {
					getHibernateTemplate().persist(new Session());
					sessionId = loadLastSession().getId().toString();
				}
			}
		}
		return sessionId;
	}

    @Override
    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    @Override
    public String getSessionComment() {
        return sessionComment;
    }

    public void setSessionComment(String sessionComment) {
        this.sessionComment = sessionComment;
    }

    @SuppressWarnings("unchecked")
	private Session loadLastSession() {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<Session> sessions = (List<Session>) hibernateTemplate.find("from Session s order by s.id desc");

		if (sessions.isEmpty()) {
			throw new TechnicalException("No session detected");
		}
		return sessions.get(0);
	}

}
