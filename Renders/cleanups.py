from os.path import isfile, join, isdir
from os import listdir, rmdir, remove

def cleanup(threshold=1000, folders=[], all=True, ignored_folder="CompleteRenders"):
    if all:
        folders = [f for f in listdir("./") if (isdir(f) and f != ignored_folder)]

    files = [join(folder, file) for folder in folders for file in listdir(folder) if isfile(join(folder, file))]
    files_to_delete = [file for file in files if int(file.split("x")[2].split(")")[0]) < threshold]
    for file in files_to_delete:
        remove(file)
    for folder in folders:
        if len(listdir(folder)):
            rmdir(folder)

cleanup()

folders = []
