#!/bin/bash

function error_exit
{
  echo -e "\e[01;31m$1\e[00m" 1>&2
  exit 1
}

if [ $CI_PULL_REQUEST == "false" ] && [ `git name-rev --name-only --refs=refs/heads/* $CI_COMMIT_ID` == "master" ]; then
  echo -e "Starting to deploy to gh-pages\n"
  
  # create and cd into temporary deployment work directory
  mkdir deployment-work
  cd deployment-work
  
  # setup git and clone from gh-pages branch
  git config --global user.email "codeship-deployer@codeaffine.com"
  git config --global user.name "Codeship Deployer"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/rherrmann/eclipse-extras.git . > /dev/null 2>&1 || error_exit "Error cloning gh-pages"
  
  # clean the repository directory, then copy the build result into it
  git rm -rf repository
  mkdir -p repository 
  cp -rf ../com.codeaffine.extras.repository/target/repository/* ./repository
  zip -r repository/eclipse-extras-repository.zip repository/*
  
  # add, commit and push files
  git add -f .
  git commit -m "[ci skip][skip ci] Deploy Codeship build $CI_BUILD_NUMBER to gh-pages"
  git push -fq origin gh-pages > /dev/null 2>&1 || error_exit "Error uploading the build result to gh-pages"
  
  # go back to the directory where we started
  cd ..
  
  echo -e "Done with deployment to gh-pages\n"
fi
