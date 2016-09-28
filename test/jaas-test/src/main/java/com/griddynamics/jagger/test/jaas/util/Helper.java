package com.griddynamics.jagger.test.jaas.util;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;

/**
 * //TODO: Could we get rid of this "potential garbage centre"?
 */
public class Helper {

    ///Had to put such a crutch to avoid messing with date formats which differ in case od DataService and JSON deserialisator responses.
    public static boolean areSessionEntitiesEqual(SessionEntity entity1, SessionEntity entity2){
            if (entity1 == entity2) return true;
            if (entity1 == null && null != entity2) return false;
            if (entity2 == null || entity1.getClass() != entity2.getClass()) return false;

            if (entity1.getComment() != null ? !entity1.getComment().equals(entity2.getComment()) : entity2.getComment()!= null) return false;
            if (entity1.getEndDate() != null ? !(entity1.getEndDate().getTime()==entity2.getEndDate().getTime()) : entity2.getEndDate()!= null) return false;
            if (entity1.getStartDate() != null ? !(entity1.getStartDate().getTime()==entity2.getStartDate().getTime()) : entity2.getStartDate()!= null) return false;
            if (entity1.getId() != null ? !entity1.getId().equals(entity2.getId()) : entity2.getId() != null) return false;
            if (entity1.getKernels() != null ? !entity1.getKernels().equals(entity2.getKernels()) : entity2.getKernels() != null) return false;

            return true;
    }
}
