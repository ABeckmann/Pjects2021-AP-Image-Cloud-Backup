// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.5.10;

// Defines a contract named `HelloWorld`.
// A contract is a collection of functions and data (its state).
// Once deployed, a contract resides at a specific address on the Ethereum blockchain.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/structure-of-a-contract.html
contract UploadImage {

    // Declares a state variable `message` of type `string`.
    // State variables are variables whose values are permanently stored in contract storage.
    // The keyword `public` makes variables accessible from outside a contract
    // and creates a function that other contracts or clients can call to access the value.
    string public message;

    // Similar to many class-based object-oriented languages, a constructor is
    // a special function that is only executed upon contract creation.
    // Constructors are used to initialize the contract's data.
    // Learn more: https://solidity.readthedocs.io/en/v0.5.10/contracts.html#constructors
    constructor() public {
        // Accepts a string argument `initMessage` and sets the value
        // into the contract's `message` storage variable).
        message = "Test string";
    }

    // A public function that accepts a string argument
    // and updates the `message` storage variable.
    function storeHash(string memory newMessage) public {
        message = newMessage;
    }

    function getHash() public view returns (string memory) {
      return message;
    }

    function getAnInt() public view returns (int) {
      return 10;
    }



    bytes32 public x;

    function storeHashTest(bytes32 newMessage) public {
        x = newMessage;
        arr.push(x);
    }

    function getTest() public returns(bytes32){
      return x;
    }

     // Declaring state variable
    bytes32[] private arr;

    // Function to add data
    // in dynamic array
    function addData(bytes32 hash) public
    {
      arr.push(hash);
    }

    // Function to get data of
    // dynamic array
    function getData() public view returns(bytes32)
    {
      return arr[0];
    }

    // Function to return length
    // of dynamic array
    function getLength() public view returns (uint)
    {
      return arr.length;
    }


    // Function to search an
    // element in dynamic array
    function search(bytes32 hash) public view returns(bool)
    {
      uint i;

      for(i = 0; i < arr.length; i++)
      {
        if(arr[i] == hash)
        {
          return true;
        }
      }

      if(i >= arr.length)
        return false;
    }



}
