// Liam May
// COP 3503
// Spring 2020
// NID: li649708

// This is an implementation of a skip list data structure that can hold any
// comparable data. The minimum height a Node can have will be 1.

//==============================================================================
// Questions:
//
// Am I not writing with Generics correctly? Compiler does not want to compare
// AnyType on 82.
//
// Either I do need head for trim() and don't understand scope well enough to
// get access to it, or I don't need it but can't figure out a method that doesn't
// need it
//==============================================================================

import java.io.*;
import java.util.*;

// Node class's data field can hold any type of comparable data and can have a
// varying number of links to other Nodes.
class Node<AnyType>
{
  int height;
  AnyType data;
  ArrayList<Node> nextReferences = new ArrayList<Node>();

  // Constructor method that creates Node object of height 'height'
  Node(int height)
  {
    // this.height = height;
    for (int i = 0; i < height; i++)
    {
      nextReferences.add(null);
    }
  }

  // Constructor method that creates Node object of height 'height' and with
  // 'data' in the data field
  Node(AnyType data, int height)
  {
    this.data = data;
    // this.height = height;
    for (int i = 0; i < height; i++)
    {
      nextReferences.add(null);
    }
  }

  // Returns value stored at the node
  public AnyType value()
  {
    return data;
  }

  // Returns height of the node
  public int height()
  {
    return nextReferences.size();
  }

  // returns the reference to next Node at the given level
  public Node<AnyType> next(int level)
  {
    return nextReferences.get(level);
  }

  // sets next reference at the given level within this node to node
  // finds the level given recursively
  public void setNext(int level, Node<AnyType> node)
  {
    nextReferences.set(level, node);
  }

  // Grows this node by exactly one level and changes references as needed
  public void grow(Node<AnyType> current, Node<AnyType> head)
  {
    current.height += 1;

    // Seeing if the growth breaks a previous reference in the list and if so,
    // connect references
    Node<AnyType> temp = head;
    while (true)
    {
      if (temp.next(current.height) == null)
      {
        current.nextReferences.add(current.height, null);
        return;
      }
      if (temp.next(current.height).data > current.data)
      {
        current.nextReferences.add(current.height, temp.next(current.height));
        return;
      }
      if (temp.next(current.height).data < current.data)
      {
        temp = temp.next(current.height);
      }
    }
  }

  // Grows node by one level with probability of 50%
  public void maybeGrow(Node<AnyType> current, Node<AnyType> head)
  {
    // Generating random number greater than or equal to 1 and less than 100
    int rand = (int)(Math.random() * 100);
    // Giving the possibility of calling grow 50%
    if (rand % 2 == 0)
      grow(current, head);

    return;
  }

  // Removes nextReferences from the top of this node's tower of next nextReferences
  // until this node's height has been reduced to the value given
  public void trim(int height)
  {
    // Getting the difference between trim height and current node height
    // int diff = this.height - height;

    // // Capturing all references in arraylist that will be moved
    // for (int i = diff; i <= this.height; i++)
    // {
    //
    // }

    // Move all references
    // Trimming the node
    int i, current = height();
    for (i = current; i > height; i--)
    {
      nextReferences.remove(i);
    }
    this.height = height;
    return;
  }
}

public class SkipList<AnyType extends Comparable<AnyType>>
{
  int size;
  int level;
  Node<AnyType> head;

  // constructs skip list with head height 1 (default constructor)
  public SkipList()
  {
    head = new Node<>(1);
    size = 1;
    level = 1;
  }

  // constructs skip list with head height of height
  public SkipList(int height)
  {
    head = new Node<>(height);
    size = 1;
    level = height;
  }

  // return number of nodes in list in O(1) time
  public int size()
  {
    return size; // <--scope error
  }

  // return height of skip list in O(1) time
  public int height()
  {
    return head.height; // <-- scope error
  }

  // generates random height of node and determines if list expansion is necessary
  public void insert(AnyType data)
  {
    int maxHeight = getMaxHeight(size);
    int newMaxHeight = getMaxHeight(size + 1);
    Node<AnyType> temp = head;

    // If logn + 1 exceeds the current maximum height, the maximum height must
    // increase.
    // Expaning height (if necessary)
    if (newMaxHeight > maxHeight)
    {
      growSkipList(maxHeight, newMaxHeight);
    }

    // Creating node of random height
    int rand = generateRandomHeight(newMaxHeight);
    Node<AnyType> newNode = new Node<>(data, rand);

    // Searching for place to put node
    temp = head;
    int i, level = head.height;
    // Links keeps track of the nodes at which we dropped down a level because
    // those are the nodes which will connect to newNode (excluding the nodes taller
    // than newNode of course)
    ArrayList<Node> links = new ArrayList<>();
    while (true)
    {
      if (temp.next(level) == null || temp.next(level).data >= data)
      {
        if (level == 1)
        {
          // we found the place of insertion
          links.add(level, temp);
          break;
        }
        links.add(level, temp);
        level--;
      }
    }

    // Inserting without losing references
    // First connecting original link destinations to newNode (as the source)
    for (i = 1; i <= rand; i++)
    {
      newNode.nextReferences.set(i, links.get(i));
    }
    // Then connecting original link sources to newNode (as the destination)
    for (i = 1; i < rand; i++)
    {
      links.get(i).nextReferences.set(i, newNode);
    }
    // keeping size up to date
    size++;
  }

  // insert node of given height
  public void insert(AnyType data, int height)
  {
    int maxHeight = getMaxHeight(size);
    int newMaxHeight = getMaxHeight(size + 1);
    Node<AnyType> temp = head;

    // If logn + 1 exceeds the current maximum height, the maximum height must
    // increase.
    // Expaning height (if necessary)
    if (newMaxHeight > maxHeight)
    {
      growSkipList(maxHeight, newMaxHeight);
    }

    // Creating node of set height
    Node<AnyType> newNode = new Node<>(data, height);

    // Searching for place to put node
    temp = head;
    int i, level = height;
    // Links keeps track of the nodes at which we dropped down a level because
    // those are the nodes which will connect to newNode (excluding the nodes taller
    // than newNode of course)
    ArrayList<Node> links = new ArrayList<>();
    while (true)
    {
      if (temp.next(level) == null || temp.next(level).data >= data)
      {
        if (level == 1)
        {
          // we found the place of insertion
          links.add(level, temp);
          break;
        }
        links.add(level, temp);
        level--;
      }
    }

    // Inserting without losing references
    // First connecting original link destinations to newNode (as the source)
    for (i = 1; i <= height; i++)
    {
      newNode.nextReferences.set(i, links.get(i));
    }
    // Then connecting original link sources to newNode (as the destination)
    for (i = 1; i < height; i++)
    {
      links.get(i).nextReferences.set(i, newNode);
    }
    // keeping size up to date
    size++;
  }

  // delete first occurence of data from skip list in O(logn) time
  // determines if trimming of max height is necessary
  public void delete(AnyType data)
  {
    if (!contains(data))
      return;

    // Capturing first occurence of data in skip list
    Node<AnyType> marked = SkipList.get(data);
    // Capturing all references to the node with data
    Node<AnyType> temp = head;
    int level = head.height;
    while(true)
    {
      
    }

  }

  // returns true or false in O(logn) time where n is the size of the list
  public boolean contains(AnyType data)
  {
    // start at top level of head node
    Node<AnyType> temp = head;
    int level = head.height;

    while (temp.next(level).data != data)
    {
      // drop down in the case you reach data > data or null
      // repeat until you reach data > data on level 1 or null
      if (temp.next(level).data == null || temp.data > temp.next(level).data)
      {
        if (level == 1)
        {
          return false;
        }
        level--;
      }
      if (temp.data < temp.next(level).data)
      {
        temp = temp.next(level);
      }
    }
    return true;
  }

  // returns reference to node with data passed
  public Node<AnyType> get(AnyType data)
  {
    // start search at top level of head
    // if data == data, return reference
    // if data > data, drop down a level and continue search
    // repeat until data found or you skip to a value greater than data or null
    // on the bottom level (in which case return null)
  }

  // returns the max height of a skip list with n nodes
  private static int getMaxHeight(int n)
  {
    return (int)(Math.log(n) / Math.log(2)) + 1;
  }

  // returns randomly generated max height (logarithmically)
  private static int generateRandomHeight(int maxHeight)
  {
    for (int i = 1; i <= maxHeight; i++)
    {
      // generating large random number
      int rand = (int)(Math.random() * 100);
      if (rand % 2 == 0)
      {
        return i;
      }
    }
  }

  // Grows list using method from insert
  private void growSkipList(int maxHeight, int newMaxHeight)
  {
    Node<AnyType> temp = head;
    // iterate through all nodes of maxHeight and maybeGrow them
    while (temp.next(maxHeight) != null)
    {
      temp = temp.next(maxHeight);
      // the reassignment of the references of the non-head nodes [will be] handled
      // within maybeGrow / grow
      temp.maybeGrow(temp, head);
    }
    // growing head last
    head.height = newMaxHeight;
    head.nextReferences.add(null);
    // linking head to the next node of newMaxHeight (if it exists)
    if (head.next(newMaxHeight) == null)
    {
      head.nextReferences.add(newMaxHeight, null);
    }
    else
    {
      head.nextReferences.add(newMaxHeight, head.next(newMaxHeight));
    }
  }

  // trims list using method from delete
  private void trimSkipList()
  {

  }
}
