package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;

    @Transactional //means do in go , not partially (atomicity)
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user  = userService.findByUserName(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            //put the saved journal entry into the particular user
            user.getJournalEntries().add(saved); // as of now this is inmemory so push to the Db
            userService.saveEntry(user);

        } catch(Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occured while saving the entry, ",e);
        }




    }
    public void saveEntry(JournalEntry journalEntry) {
       journalEntryRepository.save(journalEntry);

    }

    public List<JournalEntry> getAll () {
       return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public void deleteById(ObjectId id, String username) {
        //find the user in db and remove the object id of journal entry if get it ,and then save the user.
        User user = userService.findByUserName(username);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        userService.saveEntry(user);
        journalEntryRepository.deleteById(id);
    }

}


//controller --> service --> repository (flow of data)