const tagList = [];

const addToList = (inputList) => {
    if(inputList === undefined) {
        inputList = [];
    }
    inputList.forEach(element => {
        tagList.push(element);
    });
}

const printList = () => {
    console.log("print taglist: ", tagList);
}

const addNewToList = (newTag) => {
    if(!tagList.includes(newTag)){
        tagList.push(newTag);
    }
    else{
        console.log(`${newTag} already exists in list`);
    }
}

const getList = () => {
    return tagList;
}

export { addToList, printList, addNewToList, getList };