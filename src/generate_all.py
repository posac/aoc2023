import sre_yield
with open('generated.txt', 'w') as f:
    f.writelines(list(sre_yield.AllStrings('....#....0..\n', charset='#0')))
