/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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

package com.griddynamics.jagger.providers.creators;

import com.google.common.base.Preconditions;

/** Takes row data and returns concatenation of it
 * @author Nikolay Musienko
 * @n
 *
 * @ingroup Main_Providers_Base_group */
public class StringCreator implements ObjectCreator<String> {

    /** Creates string of concatenation
     * @author Nikolay Musienko
     * @n
     *
     * @param strings - row data
     * @return some string*/
    @Override
    public String createObject(final String... strings) {
        Preconditions.checkNotNull(strings);
        Preconditions.checkState(strings.length > 0);
        if(strings.length == 1) {
            return strings[0];
        }
        return buildString(strings);
    }

    /** Do nothing
     * @author Nikolay Musienko
     * @n
     * @par Details:
     * @details There is no case to use header
     *
     * @param header - array of columns names*/
    @Override
    public void setHeader(final String[] header) {
    }

    private String buildString(final String[] strings) {
        StringBuilder builder = new StringBuilder();
        for (String s: strings){
            builder.append(s);
        }
        return builder.toString();
    }
}
