package com.loviagin.rollic.workers;

import static com.loviagin.rollic.Constants.NICKNAME;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Random;

public class NicknameGenerator {
    private static final String[] adjectives = {"Happy", "Sad", "Brilliant", "Clever", "Enthusiastic", "Generous", "Gentle", "Curious", "Energetic", "Courageous", "Adventurous", "Ambitious", "Charming", "Gracious", "Honest", "Loyal", "Witty", "Creative", "Calm", "Confident", "Kind", "Optimistic", "Patient", "Friendly", "Smart", "Reliable", "Passionate", "Wise", "Playful", "Sincere", "Thoughtful", "Persistent", "Resilient", "Inquisitive", "Imaginative", "Resourceful", "Independent", "Spontaneous", "Courteous", "Empathetic", "Helpful", "Cheerful", "Inspiring", "Motivated", "Modest", "Ambient", "Grateful", "Meticulous", "Innovative", "Efficient", "Lively", "Joyful", "Graceful", "Warm", "Serene", "Authentic", "Brave", "Respectful", "Relaxed", "Versatile", "Humble", "Radiant", "Dynamic", "Captivating", "Exuberant", "Elegant", "Harmonious", "Spirited", "Genuine", "Discerning", "Trustworthy", "Vibrant", "Sensible", "Candid", "Adaptable", "Compassionate", "Punctual", "Impeccable", "Steadfast", "Sophisticated", "Eloquent", "Composed", "Enchanting", "Optimistic", "Resolute", "Diligent", "Polite", "Decisive", "Glorious", "Refreshing", "Endearing", "Empowered", "Nurturing", "Unwavering", "Responsible"};
    private static final String[] nouns = {"Dog", "Cat", "Elephant", "Lion", "Tiger", "Giraffe", "Zebra", "Horse", "Cow", "Sheep", "Goat", "Pig", "Rabbit", "Kangaroo", "Koala", "Dolphin", "Whale", "Shark", "Octopus", "Squid", "Seal", "Penguin", "Bear", "Deer", "Wolf", "Fox", "Raccoon", "Squirrel", "Owl", "Eagle", "Peacock", "Parrot", "Flamingo", "Pigeon", "Butterfly", "Bee", "Ant", "Spider", "Snake", "Crocodile", "Turtle", "Frog", "Fish", "Hippopotamus", "Gorilla", "Monkey", "Chimpanzee", "Panda", "Koala", "Kangaroo", "Gazelle", "Cheetah", "Leopard", "Jaguar", "Bison", "Rhino", "Hedgehog", "Lizard", "Iguana", "Camel", "Donkey", "Ostrich", "Sloth", "Armadillo", "Giraffe", "Walrus", "Bat", "Beaver", "Crab", "Jellyfish", "Lobster", "Seahorse", "Starfish", "Polar bear", "Hummingbird", "Toucan", "Puffin", "Raven", "Moose", "Koala", "Chameleon", "Platypus", "Meerkat", "Otter", "Wombat", "Tarantula", "Kookaburra", "Chinchilla", "Alpaca", "Cockatoo"};

    public static String generateRandomNickname() {
        Random random = new Random();
        String adjective = adjectives[random.nextInt(adjectives.length)];
        String noun = nouns[random.nextInt(nouns.length)];
        int number = random.nextInt(1000);
        String r = adjective + noun + number;
        if (check(r) > 0){
            generateRandomNickname();
        }
        return r;
    }

    private static int check(String usr){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(USERS_COLLECTION).whereEqualTo(NICKNAME, usr);
        AggregateQuery countQuery = query.count();
        final int[] cnt = {0};
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                cnt[0] = (int) snapshot.getCount();
            } else {
                generateRandomNickname();
            }
        });
        return cnt[0];
    }
}
