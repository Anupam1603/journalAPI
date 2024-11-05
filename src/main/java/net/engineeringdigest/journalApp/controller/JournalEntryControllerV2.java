package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

    @GetMapping("{username}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String username) {
        User user = userService.findByUserName(username);
        //Get all the journal entries under a particular user!
        List<JournalEntry> all = user.getJournalEntries();
        if(all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

     @PostMapping("{username}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String username) {

        try {

            journalEntryService.saveEntry(myEntry,username);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


     }
    @GetMapping("id/{myId}")
     public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry>journalEntry = journalEntryService.findById(myId);
        if(journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
     }

     @DeleteMapping("id/{username}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId,@PathVariable String username) {
         journalEntryService.deleteById(myId,username);
        return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
     }

     @PutMapping("id/{username}/{id}")
    public  ResponseEntity<?> updateJournalEntryById(
            @PathVariable ObjectId id,
            @RequestBody JournalEntry newEntry,
            @PathVariable String username
     ) {
        //find the id you want to update
         JournalEntry old = journalEntryService.findById(id).orElse(null);

         if(old != null) {
             //update the title or content accordingly
             old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
             old.setContent(newEntry.getContent()!= null && !newEntry.getContent().equals("") ? newEntry.getContent(): old.getContent());
             journalEntryService.saveEntry(old);
             return new ResponseEntity<>(old,HttpStatus.OK);
         }


         return new ResponseEntity<>(HttpStatus.NOT_FOUND); // whatever u want to update is not found

     }
}
