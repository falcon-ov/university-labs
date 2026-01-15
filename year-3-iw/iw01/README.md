# IW01: Writing a Simple Shell Script for Task Automation

**Completed by: Socolov Daniil**\
**Group: I2302**\
**Date: September 10**

## Objective

Learn to create and execute simple Shell scripts to automate routine
tasks in the Linux operating system.

## Task

Develop a script `cleanup.sh` that: 
- Takes at least one argument: the
path to the directory to clean up; 
- Optional arguments specify the
types of files to delete (e.g., `.tmp`, `.log`); 
- By default, deletes
files with the `.tmp` extension; 
- At the end, outputs the number of
deleted files; 
- Checks that the specified directory exists and outputs
appropriate error messages.

## Work Description

1.  ### Script Creation
    A file `cleanup.sh` was created and the code implementing the
    requirements was written.

    ![img](/images/img_1.png)

2.  ### Setting Execution Permissions
    Execution permissions were granted to the script using the command:

    ![img](/images/img_2.png)

3.  ### Testing the Script

    - #### Check deleting `.tmp` files by default:

    I created a test directory and added a few temporary files:
    ![img](/images/img_3.png)

    Then, I ran the cleanup script with just the directory as an argument:
    ![img](/images/img_4.png)

    Expected and Observed Result:
    All .tmp files (file1.tmp, file2.tmp) were successfully deleted
    The .log file (file3.log) remained untouched
    Output: Deleted 2 files.
    ![img](/images/img_5.png)

    - #### Check deleting other file types (e.g., `.log`)

    I created new .log and .tmp files in the same directory:
    ![img](/images/img_6.png)

    Then, I executed the cleanup script with the extension specified:

    ![img](/images/img_7.png)

    Expected and Observed Result:
    Only the .log files (error1.log, error2.log, file3.log) were deleted
    Other files remained untouched
    Output: Deleted 3 files.

    - #### Check handling of a non-existent directory.

    I ran the cleanup script with a directory that doesn't exist:
    ![img](/images/img_8.png)

    - #### Check multi-extension cleanup

    I added more files to the test directory:
    ![img](/images/img_9.png)

    Then, I ran the cleanup script with multiple extensions specified:
    ![img](/images/img_10.png)

    Expected and Observed Result:

    All three file types (.tmp, .log, .bak) were successfully deleted
    No unrelated files were affected
    Output: Deleted 5 files.

## Conclusion

During this work, the `cleanup.sh` script was created and tested,
allowing automatic deletion of temporary files from a specified
directory. This work helped consolidate skills in writing and executing
Shell scripts, as well as handling arguments and errors.
