package io.muzoo.ssc.project.backend.chat;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.group.Group;
import io.muzoo.ssc.project.backend.group.GroupDTO;
import io.muzoo.ssc.project.backend.group.GroupRepository;
import io.muzoo.ssc.project.backend.user.User;
import io.muzoo.ssc.project.backend.user.UserRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.ws.rs.PathParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
public class ChatController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ChatRepository chatRepository;

    @PostMapping("/chat/all")
    public GroupDTO fetchChats(@RequestParam String username) {
        User user = userRepository.findFirstByUsername(username);
        Set<Group> groupSet = user.getGroups();
        JsonArrayBuilder groups = Json.createArrayBuilder();
        for (Group group : groupSet) {
            groups.add(Json.createObjectBuilder()
                    .add("groupId", group.getId())
                    .add("name", group.getName()));
        }
        return GroupDTO
                .builder()
                .success(true)
                .groups(groups.build())
                .build();
    }

    @PostMapping("/user/chat/{groupId}/send")
    public SimpleResponseDTO sendMessage(@PathVariable("groupId")String groupId,
                                         @RequestParam String username,
                                         @RequestParam String message) {
        User user = userRepository.findFirstByUsername(username);
        Group group = groupRepository.findById(groupId);
        if (user.getGroups().contains(group)) {
            Chat newMsg = new Chat();
            newMsg.setGroup(group);
            newMsg.setMessage(message);
            Timestamp time = Timestamp.valueOf(LocalDateTime.now());
            String hash = DigestUtils.sha256Hex(String.format("%s%s", groupId, time));
            newMsg.setSender(user);
            newMsg.setMessageId(hash);
            newMsg.setSent(time);
            chatRepository.save(newMsg);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("Message sent.")
                    .build();
        }
        return SimpleResponseDTO
                .builder()
                .success(false)
                .message("You are not in this group!")
                .build();
    }

    @PostMapping("/user/chat/{groupId}")
    public ChatDTO fetchGroupChat(@PathVariable("groupId")String groupId,
                                 @RequestParam String username) {
        Group group = groupRepository.findById(groupId);
        User user = userRepository.findFirstByUsername(username);
        if (user.getGroups().contains(group)) {
            List<Chat> chatList = chatRepository.findAllByGroupOrderBySent(group);
            JsonArrayBuilder chats = Json.createArrayBuilder();
            for (Chat chat : chatList) {
                chats.add(Json.createObjectBuilder()
                                .add("messageId", chat.getMessageId())
                                .add("message", chat.getMessage())
                                .add("sender", chat.getSender().getUsername())
                                .add("sent", chat.getSent().toString())
                );
            }
            return ChatDTO
                    .builder()
                    .success(true)
                    .chat(chats.build())
                    .build();
        }
        return ChatDTO
                .builder()
                .success(false)
                .chat(null)
                .build();
    }
}
