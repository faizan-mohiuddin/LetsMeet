package com.LetsMeet.LetsMeet.Event.Poll;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Poll.Model.*;
import com.LetsMeet.LetsMeet.Event.Poll.Model.PollResponses;
import com.LetsMeet.LetsMeet.Root.Permission.PermissionService;
import com.LetsMeet.LetsMeet.Root.Permission.Model.Permissions;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PollService {
    
    @Autowired
    PermissionService permissionService;

    @Autowired
    PollDAO pollDAO;

    @Autowired
    PollResponseDAO pollResponseDAO;

    public boolean create(User user, Poll poll){

        // Set Users permissions
        permissionService.setPermission(Permissions.create(user.getUUID(), poll.getUUID(), "0600"));
        
        // Save poll
        savePoll(poll);

        return true;
    }

    public boolean update(Poll poll) throws IllegalArgumentException{
        try {
            return pollDAO.update(poll);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not update Poll<"+poll.getUUID()+"> : " + e.getMessage());
        }
    }


    public boolean addResponse(Poll poll, List<String> choices){
        PollResponse response = PollResponses.forPoll(poll, choices);
        return addResponse(poll, response);
    }
    
    public boolean addResponse(Poll poll, PollResponse response){
        try{
            // Increase values in the poll
            for (var choice : response.entrySet()){
                if ( choice.getValue() ){
                    int count = poll.containsKey(choice.getKey()) ? poll.get(choice.getKey()) : 0;
                    poll.put(choice.getKey(), ++count);

                    if ( !poll.getSelectMultiple() ){break;}
                }
            }

            pollResponseDAO.save(response);
            return pollDAO.update(poll);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Could not add response," + e.getMessage());
        }
    }

    public boolean removeResponse(Poll poll, PollResponse response){
        try{
            for (var choice : response.entrySet()){
                if ( choice.getValue() ){
                    int count = poll.containsKey(choice.getKey()) ? poll.get(choice.getKey()) : 0;
                    poll.put(choice.getKey(), --count);
                }
            }
            return pollDAO.update(poll);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Could not remove response," + e.getMessage());
        }
    }

    public boolean updateResponse(Poll poll, PollResponse old, List<String> choices){
        PollResponse replacement = PollResponses.forPoll(poll, choices);
        return updateResponse(poll, old, replacement);
    }

    public boolean updateResponse(Poll poll, PollResponse old, PollResponse replacement){
        try{
            removeResponse(poll, old);
            addResponse(poll, replacement);
            return true;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Could not update response," + e.getMessage());
        }
    }



    public Optional<Poll> getPoll(UUID pollUUID) throws IllegalArgumentException{
        try{
            return pollDAO.get(pollUUID);
        }
        catch( Exception e){
            throw new IllegalArgumentException("Unable to get Poll: " + e.getMessage());
        }
    }

    public PollResponse getResponse(UUID responseUUID){
        try{
            return pollResponseDAO.get(responseUUID).orElseThrow();
        }
        catch( Exception e){
            throw new IllegalArgumentException("Unable to get PollResponse: " + e.getMessage());
        }
    }

    public boolean savePoll(Poll poll){
        try{
            pollDAO.save(poll);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
