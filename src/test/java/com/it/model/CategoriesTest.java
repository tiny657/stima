package com.it.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class CategoriesTest {

    @Test
    public void addCategory() {
        // Given
        Categories categories = new Categories();
        categories.add("category1");
        categories.add("category2");
        categories.add("category3");

        // When
        Set<String> categoryNames = categories.getCategoryNames();

        // Then
        assertThat(categoryNames.size(), is(3));
    }

    @Test
    public void removeCategory() {
        // Given
        Categories categories = new Categories();
        categories.add("category1");
        categories.add("category2");
        categories.add("category3");
        categories.remove("category1");
        categories.remove("category4");

        // When
        int size = categories.getCategoryNames().size();

        // Then
        assertThat(size, is(2));
    }

    @Test
    public void addMember() {
        // Given
        Categories categories = new Categories();
        categories.add("category1", new Member("host", 1001));
        categories.add("category1", new Member("host", 1002));
        categories.add("category2");
        categories.add("category2", new Member("host", 1003));

        // When
        int size = categories.getCategoryNames().size();

        // Then
        assertThat(size, is(2));
    }

    @Test
    public void setStatus() {
        // Given
        Categories categories = new Categories();
        categories.add("category1", new Member("host", 1001));
        categories.add("category1", new Member("host", 1002));
        categories.add("category2", new Member("host", 1003));
        categories.add("category2", new Member("host", 1004));

        // When
        boolean result1 = categories.setStatus(new Member("host", 1001), true);
        boolean result2 = categories.setStatus(new Member("host", 1001), true);
        boolean result3 = categories.setStatus(new Member("host", 1001), false);
        boolean result4 = categories.setStatus(new Member("host", 1004), true);
        boolean result5 = categories.setStatus(new Member("host", 1005), true);

        // Then
        assertThat(result1, is(true));
        assertThat(result2, is(true));
        assertThat(result3, is(true));
        assertThat(result4, is(true));
        assertThat(result5, is(false));
    }

    @Test
    public void notEqualsToCategoryCount() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));
        categories1.add("category2", new Member("host", 1003));
        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));

        // When
        boolean equals = categories1.equals(categories2);

        // Then
        assertThat(equals, is(false));
    }

    @Test
    public void notEqualsToMemberCount() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));
        categories1.add("category1", new Member("host", 1003));
        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));

        // When
        boolean equals = categories1.equals(categories2);

        // Then
        assertThat(equals, is(false));
    }

    @Test
    public void notEqualsToPort() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));
        categories1.add("category1", new Member("host", 1003));
        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));
        categories2.add("category1", new Member("host", 1004));

        // When
        boolean equals = categories1.equals(categories2);

        // Then
        assertThat(equals, is(false));
    }

    @Test
    public void notEqualsToHost() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));
        categories1.add("category1", new Member("host", 1003));
        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));
        categories2.add("category1", new Member("host2", 1003));

        // When
        boolean equals = categories1.equals(categories2);

        // Then
        assertThat(equals, is(false));
    }

    @Test
    public void equals() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));
        categories1.add("category2", new Member("host", 1003));
        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));
        categories2.add("category2", new Member("host", 1003));

        // When
        boolean equals = categories1.equals(categories2);

        // Then
        assertThat(equals, is(true));
    }

    @Test
    public void diffCategory() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));
        categories1.add("category2", new Member("host", 1003));

        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));

        // When
        Map<String, MemberList> diff = categories1.diff(categories2);

        // Then
        assertThat(diff.get("category1").getMembers().size(), is(0));
        assertThat(diff.get("category2").getMembers().size(), is(1));
    }

    @Test
    public void diffMember() {
        // Given
        Categories categories1 = new Categories();
        categories1.add("category1", new Member("host", 1001));
        categories1.add("category1", new Member("host", 1002));

        Categories categories2 = new Categories();
        categories2.add("category1", new Member("host", 1001));
        categories2.add("category1", new Member("host", 1002));
        categories2.add("category1", new Member("host", 1003));
        categories2.add("category2", new Member("host", 1004));

        // When
        Map<String, MemberList> diff = categories1.diff(categories2);

        // Then
        assertThat(diff.get("category1").getMembers().size(), is(0));
    }
}
