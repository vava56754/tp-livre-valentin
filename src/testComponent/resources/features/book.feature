Feature: store and get books
  Scenario: the user creates two books and retrieves both
    Given the user creates the book "Toto" by "Author1"
    And the user creates the book "Tata" by "Author2"
    When the user gets all books
    Then the list should contain the following books
      | title | author   |
      | Toto  | Author1  |
      | Tata  | Author2  |

Scenario: the user reserves a book
  Given the user creates the book "Toto" by "Author1"
  When the user reserves the book with id 1
  Then the reservation status of the book with id 1 should be "true"
