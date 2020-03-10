import os
import time

repos_dir = "/root/repos/repos41"
if not os.path.exists(repos_dir):
    os.mkdir(repos_dir)
def clone_repos():
    index = 0
    with open("repos_30121_final_2.csv", "r", encoding="utf-8") as fr:
        lines = fr.readlines()
        print(len(lines))
        for line in lines[2400:2580]:
            line_list = line.strip().split(",")
            assert len(line_list) == 6
            url = line_list[4]
            project_name = line_list[0]
            repo_dir = os.path.join(repos_dir, project_name)
            os.mkdir(repo_dir)
            index += 1
            print(index, url)
            try:
                os.system("git clone " + url + " " + repo_dir)
                print("git clone " + url)
                time.sleep(1)
            except Exception as e:
                print(e)
    print(index)


clone_repos()
