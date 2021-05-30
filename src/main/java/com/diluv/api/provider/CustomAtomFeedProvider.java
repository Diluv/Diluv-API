package com.diluv.api.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.resteasy.plugins.providers.atom.AtomFeedProvider;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBMarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;
import org.jboss.resteasy.plugins.providers.resteasy_atom.i18n.Messages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * This class was created by <Bill Burke>. It's distributed as
 * part of Resteasy. Get the Source Code in github:
 * https://github.com/resteasy/Resteasy/
 *
 * Resteasy is Apache License 2.0 and distributed under the
 * Resteasy License: https://github.com/resteasy/Resteasy/blob/master/License.html
 *
 * Changes
 * - Removed if check from getPreferredPrefix to not include a prefix
 */
public class CustomAtomFeedProvider extends AtomFeedProvider {

    @Override
    public void writeTo (Feed feed, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        JAXBContextFinder finder = getFinder(mediaType);
        if (finder == null) {
            throw new JAXBUnmarshalException(Messages.MESSAGES.unableToFindJAXBContext(mediaType));
        }
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Feed.class);
        for (Entry entry : feed.getEntries()) {
            if (entry.getAnyOtherJAXBObject() != null) {
                set.add(entry.getAnyOtherJAXBObject().getClass());
            }
            if (entry.getContent() != null && entry.getContent().getJAXBObject() != null) {
                set.add(entry.getContent().getJAXBObject().getClass());
            }
        }
        try {
            JAXBContext ctx = finder.findCacheContext(mediaType, annotations, set.toArray(new Class[0]));
            Marshaller marshaller = ctx.createMarshaller();
            NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
                @Override
                public String getPreferredPrefix (String namespace, String s1, boolean b) {

                    return s1;
                }
            };

            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);

            marshaller.marshal(feed, entityStream);
        }
        catch (JAXBException e) {
            throw new JAXBMarshalException(Messages.MESSAGES.unableToMarshal(mediaType), e);
        }
    }
}
