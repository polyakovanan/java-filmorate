package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFriendshipStorage")
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Set<Friendship> friendships = new HashSet<>();

    @Override
    public Set<Long> getFriendsByUserId(long id) {
        return friendships.stream()
                .filter(friendship -> friendship.getUserId() == id && friendship.isAccepted())
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
    }

    @Override
    public void create(long userId, long friendId) {
        Optional<Friendship> friendshipOp = friendships.stream()
                .filter(f -> f.getUserId() == userId && f.getFriendId() == friendId)
                .findFirst();
        if ((friendshipOp.isEmpty())) {
            friendships.add(new Friendship(userId, friendId, false));
            friendships.add(new Friendship(friendId, userId, false));
        }
    }

    @Override
    public void accept(long userId, long friendId) {
        Optional<Friendship> friendshipOp = friendships.stream()
                .filter(f -> f.getUserId() == userId && f.getFriendId() == friendId)
                .findFirst();
        if (friendshipOp.isPresent()) {
            Friendship friendship = friendshipOp.get();
            friendship.setAccepted(true);
        }

        friendshipOp = friendships.stream()
                .filter(f -> f.getUserId() == friendId && f.getFriendId() == userId)
                .findFirst();
        if (friendshipOp.isPresent()) {
            Friendship friendship = friendshipOp.get();
            friendship.setAccepted(true);
        }
    }

    @Override
    public void remove(long userId, long friendId) {
        Optional<Friendship> friendshipOp = friendships.stream()
                .filter(f -> f.getUserId() == userId && f.getFriendId() == friendId)
                .findFirst();
        friendshipOp.ifPresent(friendships::remove);

        friendshipOp = friendships.stream()
                .filter(f -> f.getUserId() == friendId && f.getFriendId() == userId)
                .findFirst();
        friendshipOp.ifPresent(friendships::remove);
    }

    @Override
    public void clear() {
        friendships.clear();
    }
}
